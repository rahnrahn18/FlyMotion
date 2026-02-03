package com.fly.motion.export

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.view.Surface
import com.fly.motion.render.EGLUtils
import com.fly.motion.render.GLRenderer
import com.fly.motion.stabilization.StabilizationEngine
import com.fly.motion.video.FrameExtractor
import com.fly.motion.video.VideoSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer

class VideoExporter(private val context: Context) {

    suspend fun export(
        source: VideoSource,
        outputFile: File,
        engine: StabilizationEngine,
        onProgress: (Float) -> Unit
    ) = withContext(Dispatchers.Default) {
        val width = source.metadata.width
        val height = source.metadata.height
        val fps = source.metadata.fps.toInt()
        val bitrate = source.metadata.bitrate.takeIf { it > 0 } ?: (width * height * 4)

        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, fps)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

        val encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        val inputSurface = encoder.createInputSurface()
        encoder.start()

        // EGL Setup
        val eglUtils = EGLUtils()
        eglUtils.init(inputSurface)

        val renderer = GLRenderer()
        renderer.init()

        val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        var trackIndex = -1
        var muxerStarted = false

        val bufferInfo = MediaCodec.BufferInfo()
        val totalFrames = (source.metadata.durationMs / 1000f * fps).toInt()
        val extractor = FrameExtractor(context, source)

        try {
            for (i in 0 until totalFrames) {
                val timeMs = (i * 1000L / fps)
                val originalBitmap = extractor.extractFrameAt(timeMs)

                if (originalBitmap != null) {
                    val stabilizedBitmap = originalBitmap.copy(originalBitmap.config, true)
                    engine.getStabilizedFrame(originalBitmap, i, stabilizedBitmap)

                    // Render to Encoder Surface
                    renderer.drawBitmap(stabilizedBitmap)
                    eglUtils.swapBuffers()

                    originalBitmap.recycle()
                    stabilizedBitmap.recycle()
                } else {
                     // Handle missing frame (skip or duplicate?)
                     // For now we continue, but timestamp might drift if we don't account.
                     // The drainEncoder loop relies on encoder generating data.
                     // If we don't swap buffers, encoder might not output.
                     // We should probably just skip.
                }

                drainEncoder(encoder, muxer, bufferInfo, false, trackIndex, muxerStarted) { index, started ->
                    trackIndex = index
                    muxerStarted = started
                }

                onProgress(i.toFloat() / totalFrames)
            }

            drainEncoder(encoder, muxer, bufferInfo, true, trackIndex, muxerStarted) { index, started ->
                 trackIndex = index
                 muxerStarted = started
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        } finally {
            try {
                eglUtils.release()
                encoder.stop()
                encoder.release()
                if (muxerStarted) {
                    muxer.stop()
                }
                muxer.release()
                extractor.release()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun drainEncoder(
        encoder: MediaCodec,
        muxer: MediaMuxer,
        bufferInfo: MediaCodec.BufferInfo,
        endOfStream: Boolean,
        trackIndex: Int,
        muxerStarted: Boolean,
        onMuxerStatus: (Int, Boolean) -> Unit
    ) {
        if (endOfStream) {
            encoder.signalEndOfInputStream()
        }

        var currentTrackIndex = trackIndex
        var currentMuxerStarted = muxerStarted

        while (true) {
            val encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, 10000)
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) break
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (currentMuxerStarted) throw RuntimeException("Format changed twice")
                val newFormat = encoder.outputFormat
                currentTrackIndex = muxer.addTrack(newFormat)
                muxer.start()
                currentMuxerStarted = true
                onMuxerStatus(currentTrackIndex, currentMuxerStarted)
            } else if (encoderStatus < 0) {
                // ignore
            } else {
                val encodedData = encoder.getOutputBuffer(encoderStatus) ?: continue
                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    bufferInfo.size = 0
                }

                if (bufferInfo.size != 0) {
                    if (!currentMuxerStarted) throw RuntimeException("Muxer not started")
                    encodedData.position(bufferInfo.offset)
                    encodedData.limit(bufferInfo.offset + bufferInfo.size)
                    muxer.writeSampleData(currentTrackIndex, encodedData, bufferInfo)
                }

                encoder.releaseOutputBuffer(encoderStatus, false)
                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break
                }
            }
        }
    }
}

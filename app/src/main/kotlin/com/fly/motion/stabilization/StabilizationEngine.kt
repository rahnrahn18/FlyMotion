package com.fly.motion.stabilization

import android.graphics.Bitmap
import com.fly.motion.nativebridge.NativeStabilizer
import com.fly.motion.video.FrameExtractor
import com.fly.motion.video.VideoSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StabilizationEngine {
    private var nativeStabilizer: NativeStabilizer? = null

    suspend fun analyze(
        videoSource: VideoSource,
        config: StabilizationConfig,
        frameExtractor: FrameExtractor,
        onProgress: (Float) -> Unit
    ): StabilizationResult = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()

        // Fix: Re-create NativeStabilizer for a fresh session
        nativeStabilizer?.release()
        nativeStabilizer = NativeStabilizer()
        val stabilizer = nativeStabilizer!!

        val fps = videoSource.metadata.fps
        val frameInterval = (1000 / fps).toLong()
        val totalDuration = videoSource.metadata.durationMs

        var processedFrames = 0
        var currentTime = 0L

        // Pass 1: Feed frames
        while (currentTime < totalDuration) {
            val bitmap = frameExtractor.extractFrameAt(currentTime)
            if (bitmap != null) {
                stabilizer.pushFrame(bitmap)
                bitmap.recycle()
                processedFrames++
            }

            currentTime += frameInterval
            onProgress(currentTime.toFloat() / totalDuration)
        }

        // Pass 2: Finalize (Smooth)
        stabilizer.finalizeAnalysis(config.smoothness)

        return@withContext StabilizationResult(
            frameCount = processedFrames,
            processingTimeMs = System.currentTimeMillis() - startTime,
            success = true
        )
    }

    fun getStabilizedFrame(original: Bitmap, index: Int, output: Bitmap) {
        nativeStabilizer?.stabilizeFrame(original, output, index)
    }

    fun release() {
        nativeStabilizer?.release()
        nativeStabilizer = null
    }
}

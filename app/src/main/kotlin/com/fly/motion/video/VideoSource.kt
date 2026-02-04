package com.fly.motion.video

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File

class VideoSource(private val context: Context, val uri: Uri) {
    var metadata: VideoMetadata = extractMetadata()
        private set

    private fun extractMetadata(): VideoMetadata {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
            val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull() ?: 0

            // FPS is not always available via MMR, assume 30 or try to parse
            // Some devices might support METADATA_KEY_CAPTURE_FRAMERATE
            val fps = 30f

            return VideoMetadata(width, height, duration, rotation, fps, bitrate)
        } catch (e: Exception) {
            e.printStackTrace()
            return VideoMetadata(0, 0, 0, 0, 30f, 0)
        } finally {
            retriever.release()
        }
    }

    fun getFilePath(): String? {
        // Simple file path resolution if uri is file://
        if (uri.scheme == "file") {
            return uri.path
        }
        return null // For content:// implementation is more complex, usually copy to cache
    }
}

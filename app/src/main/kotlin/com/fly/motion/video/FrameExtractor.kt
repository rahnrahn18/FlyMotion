package com.fly.motion.video

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FrameExtractor(private val context: Context, private val source: VideoSource) {

    private val retriever = MediaMetadataRetriever()

    init {
        retriever.setDataSource(context, source.uri)
    }

    suspend fun extractFrameAt(timeMs: Long): Bitmap? = withContext(Dispatchers.IO) {
        try {
            // OPTION_CLOSEST_SYNC is faster but less accurate. OPTION_CLOSEST is better for editing.
            return@withContext retriever.getFrameAtTime(timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    // Extract frames with a specific interval (e.g. for analysis)
    suspend fun extractFrames(intervalMs: Long, callback: (Bitmap, Int) -> Unit) = withContext(Dispatchers.IO) {
        val duration = source.metadata.durationMs
        var currentTime = 0L
        var index = 0

        while (currentTime < duration) {
            val bitmap = extractFrameAt(currentTime)
            if (bitmap != null) {
                callback(bitmap, index)
                bitmap.recycle()
            }
            currentTime += intervalMs
            index++
        }
    }

    fun release() {
        retriever.release()
    }
}

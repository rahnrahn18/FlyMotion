package com.fly.motion.video

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever

class VideoDecoder(context: Context, private val source: VideoSource) {
    private val retriever = MediaMetadataRetriever()

    init {
        retriever.setDataSource(context, source.uri)
    }

    fun getFrameIterator(stepMs: Long = 33): Iterator<Bitmap?> {
        return object : Iterator<Bitmap?> {
            var currentTime = 0L
            val duration = source.metadata.durationMs

            override fun hasNext(): Boolean {
                return currentTime < duration
            }

            override fun next(): Bitmap? {
                val frame = retriever.getFrameAtTime(currentTime * 1000, MediaMetadataRetriever.OPTION_CLOSEST)
                currentTime += stepMs
                return frame
            }
        }
    }

    fun release() {
        retriever.release()
    }
}

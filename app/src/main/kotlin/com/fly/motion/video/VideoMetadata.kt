package com.fly.motion.video

data class VideoMetadata(
    val width: Int,
    val height: Int,
    val durationMs: Long,
    val rotation: Int,
    val fps: Float,
    val bitrate: Int
)

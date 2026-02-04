package com.fly.motion.stabilization

data class StabilizationResult(
    val frameCount: Int,
    val processingTimeMs: Long,
    val success: Boolean
)

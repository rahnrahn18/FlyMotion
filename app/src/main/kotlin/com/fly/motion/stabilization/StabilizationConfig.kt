package com.fly.motion.stabilization

data class StabilizationConfig(
    val smoothness: Int = 30, // Smoothing radius
    val cropRatio: Float = 0.0f, // Not used yet, maybe for auto-cropping
    val method: StabilizationMethod = StabilizationMethod.OPTICAL_FLOW_FAST
)

enum class StabilizationMethod {
    OPTICAL_FLOW_FAST,
    FEATURE_MATCHING
}

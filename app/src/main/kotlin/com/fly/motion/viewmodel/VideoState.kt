package com.fly.motion.viewmodel

import com.fly.motion.stabilization.StabilizationResult
import com.fly.motion.video.VideoMetadata

sealed class VideoState {
    object Idle : VideoState()
    object Loading : VideoState()
    data class Loaded(val metadata: VideoMetadata) : VideoState()
    data class Analyzing(val progress: Float) : VideoState()
    data class Stabilized(val result: StabilizationResult) : VideoState()
    data class Error(val message: String) : VideoState()
}

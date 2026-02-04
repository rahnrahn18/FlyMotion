package com.fly.motion.stabilization

class AutoZoomController {
    fun calculateZoomFactor(originalWidth: Int, originalHeight: Int, cropRect: android.graphics.RectF): Float {
        val widthRatio = originalWidth / cropRect.width()
        val heightRatio = originalHeight / cropRect.height()
        return kotlin.math.max(widthRatio, heightRatio)
    }
}

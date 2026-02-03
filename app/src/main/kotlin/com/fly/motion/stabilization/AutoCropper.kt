package com.fly.motion.stabilization

import android.graphics.RectF

class AutoCropper {
    // Calculates the crop rectangle to hide borders based on max translation
    fun calculateCropRect(width: Int, height: Int, maxTx: Float, maxTy: Float): RectF {
        val cropX = maxTx
        val cropY = maxTy

        return RectF(
            cropX,
            cropY,
            width - cropX,
            height - cropY
        )
    }
}

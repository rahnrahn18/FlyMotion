package com.fly.motion.stabilization

import android.graphics.Matrix

class VideoStabilizer {
    // Pure Kotlin placeholder logic if needed, but we use Native
    fun getMatrix(trajectory: Trajectory): Matrix {
        val matrix = Matrix()
        matrix.postTranslate(trajectory.x.toFloat(), trajectory.y.toFloat())
        matrix.postRotate(Math.toDegrees(trajectory.a).toFloat())
        return matrix
    }
}

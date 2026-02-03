package com.fly.motion.stabilization

import android.graphics.Bitmap
import com.fly.motion.nativebridge.NativeMotionEstimator

class MotionEstimator {
    private val nativeEstimator = NativeMotionEstimator()

    fun estimate(prev: Bitmap, curr: Bitmap): DoubleArray {
        return nativeEstimator.estimate(prev, curr)
    }

    fun release() {
        nativeEstimator.release()
    }
}

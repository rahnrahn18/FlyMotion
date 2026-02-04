package com.fly.motion.nativebridge

import android.graphics.Bitmap

class NativeMotionEstimator {
    private var nativeHandle: Long = 0

    init {
        NativeLoader.load()
        nativeHandle = create()
    }

    fun estimate(prev: Bitmap, curr: Bitmap): DoubleArray {
        val transform = DoubleArray(6)
        if (nativeHandle != 0L) {
            estimate(nativeHandle, prev, curr, transform)
        }
        return transform
    }

    fun release() {
        if (nativeHandle != 0L) {
            release(nativeHandle)
            nativeHandle = 0
        }
    }

    private external fun create(): Long
    private external fun estimate(handle: Long, prev: Bitmap, curr: Bitmap, outTransform: DoubleArray)
    private external fun release(handle: Long)
}

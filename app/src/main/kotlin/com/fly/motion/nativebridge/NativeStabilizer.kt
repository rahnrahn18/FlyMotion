package com.fly.motion.nativebridge

import android.graphics.Bitmap

class NativeStabilizer {
    private var nativeHandle: Long = 0

    init {
        NativeLoader.load()
        nativeHandle = create()
    }

    fun pushFrame(bitmap: Bitmap) {
        if (nativeHandle != 0L) {
            pushFrame(nativeHandle, bitmap)
        }
    }

    fun finalizeAnalysis(radius: Int) {
        if (nativeHandle != 0L) {
            finalizeAnalysis(nativeHandle, radius)
        }
    }

    fun stabilizeFrame(src: Bitmap, dst: Bitmap, index: Int) {
        if (nativeHandle != 0L) {
            stabilizeFrame(nativeHandle, src, dst, index)
        }
    }

    fun getFrameCount(): Int {
        if (nativeHandle != 0L) {
            return getFrameCount(nativeHandle)
        }
        return 0
    }

    fun release() {
        if (nativeHandle != 0L) {
            release(nativeHandle)
            nativeHandle = 0
        }
    }

    protected fun finalize() {
        release()
    }

    private external fun create(): Long
    private external fun pushFrame(handle: Long, bitmap: Bitmap)
    private external fun finalizeAnalysis(handle: Long, radius: Int)
    private external fun stabilizeFrame(handle: Long, src: Bitmap, dst: Bitmap, index: Int)
    private external fun getFrameCount(handle: Long): Int
    private external fun release(handle: Long)
}

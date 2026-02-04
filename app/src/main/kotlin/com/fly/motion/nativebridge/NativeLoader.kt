package com.fly.motion.nativebridge

object NativeLoader {
    private var isLoaded = false

    fun load() {
        if (!isLoaded) {
            try {
                System.loadLibrary("flymotion")
                isLoaded = true
            } catch (e: UnsatisfiedLinkError) {
                e.printStackTrace()
            }
        }
    }
}

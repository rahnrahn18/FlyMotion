package com.fly.motion.nativebridge

import android.graphics.Bitmap

object NativeFrameProcessor {
    init {
        NativeLoader.load()
    }

    external fun convertToGrayscale(src: Bitmap, dst: Bitmap)
    external fun resize(src: Bitmap, dst: Bitmap, width: Int, height: Int)
}

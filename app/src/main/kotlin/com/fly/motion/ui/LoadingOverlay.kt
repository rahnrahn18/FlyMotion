package com.fly.motion.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.fly.motion.R
import android.view.LayoutInflater

class LoadingOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_loading, this, true)
    }

    fun show() { visibility = VISIBLE }
    fun hide() { visibility = GONE }
}

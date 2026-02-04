package com.fly.motion.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.fly.motion.databinding.ViewStabilizationControlsBinding

class StabilizationControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewStabilizationControlsBinding

    init {
        binding = ViewStabilizationControlsBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setOnStabilizeListener(listener: () -> Unit) {
        binding.btnStabilize.setOnClickListener { listener() }
    }

    // Additional methods to expose slider etc.
}

package com.fly.motion.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.fly.motion.databinding.ActivityEditorBinding
import com.fly.motion.stabilization.StabilizationConfig
import com.fly.motion.viewmodel.EditorViewModel
import com.fly.motion.viewmodel.VideoState

class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding
    private val viewModel: EditorViewModel by viewModels()
    private var isComparing = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoUri = intent.data
        if (videoUri != null) {
            viewModel.loadVideo(videoUri)
        } else {
            finish()
            return
        }

        setupUI()
        observeViewModel()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnStabilize.setOnClickListener {
            val smoothness = binding.sliderSmoothness.value.toInt()
            val config = StabilizationConfig(smoothness = smoothness)
            viewModel.startStabilization(config)
        }

        binding.btnCompare.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isComparing = true
                    updatePreview()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isComparing = false
                    updatePreview()
                    true
                }
                else -> false
            }
        }

        binding.timelineSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updatePreview()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnExport.setOnClickListener {
            // TODO: Implement Export
            Toast.makeText(this, "Exporting...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.videoState.observe(this) { state ->
            when (state) {
                is VideoState.Idle -> {
                    binding.loadingIndicator.visibility = View.GONE
                    binding.progressText.visibility = View.GONE
                }
                is VideoState.Loading -> {
                    binding.loadingIndicator.visibility = View.VISIBLE
                    binding.progressText.text = "Loading Video..."
                    binding.progressText.visibility = View.VISIBLE
                }
                is VideoState.Loaded -> {
                    binding.loadingIndicator.visibility = View.GONE
                    binding.progressText.visibility = View.GONE
                    binding.timelineSeekbar.max = state.metadata.durationMs.toInt()
                    updatePreview()
                }
                is VideoState.Analyzing -> {
                    binding.loadingIndicator.visibility = View.VISIBLE
                    binding.progressText.visibility = View.VISIBLE
                    val percent = (state.progress * 100).toInt()
                    binding.progressText.text = "Analyzing Motion... $percent%"
                    binding.btnStabilize.isEnabled = false
                }
                is VideoState.Stabilized -> {
                    binding.loadingIndicator.visibility = View.GONE
                    binding.progressText.visibility = View.GONE
                    binding.btnStabilize.isEnabled = true
                    binding.btnStabilize.text = "Re-Stabilize"
                    Toast.makeText(this, "Stabilization Complete!", Toast.LENGTH_SHORT).show()
                    updatePreview()
                }
                is VideoState.Error -> {
                    binding.loadingIndicator.visibility = View.GONE
                    binding.progressText.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    binding.btnStabilize.isEnabled = true
                }
            }
        }
    }

    private fun updatePreview() {
        val timeMs = binding.timelineSeekbar.progress.toLong()

        viewModel.requestPreviewFrame(timeMs) { original, stabilized ->
            runOnUiThread {
                if (isComparing || stabilized == null) {
                    binding.previewImage.setImageBitmap(original)
                    binding.previewLabel.text = "Original"
                } else {
                    binding.previewImage.setImageBitmap(stabilized)
                    binding.previewLabel.text = "Stabilized"
                }
            }
        }
    }
}

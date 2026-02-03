package com.fly.motion.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fly.motion.export.VideoExporter
import com.fly.motion.stabilization.StabilizationConfig
import com.fly.motion.stabilization.StabilizationEngine
import com.fly.motion.video.FrameExtractor
import com.fly.motion.video.VideoSource
import kotlinx.coroutines.launch
import java.io.File

class EditorViewModel(application: Application) : AndroidViewModel(application) {

    private val _videoState = MutableLiveData<VideoState>(VideoState.Idle)
    val videoState: LiveData<VideoState> = _videoState

    private var videoSource: VideoSource? = null
    private var frameExtractor: FrameExtractor? = null
    val stabilizationEngine = StabilizationEngine()

    // Preview Cache
    private var currentOriginalFrame: Bitmap? = null
    private var currentStabilizedFrame: Bitmap? = null

    fun loadVideo(uri: Uri) {
        viewModelScope.launch {
            _videoState.value = VideoState.Loading
            try {
                val source = VideoSource(getApplication(), uri)
                videoSource = source
                frameExtractor = FrameExtractor(getApplication(), source)
                _videoState.value = VideoState.Loaded(source.metadata)
            } catch (e: Exception) {
                _videoState.value = VideoState.Error("Failed to load video: ${e.message}")
            }
        }
    }

    fun startStabilization(config: StabilizationConfig) {
        val source = videoSource ?: return
        val extractor = frameExtractor ?: return

        viewModelScope.launch {
            _videoState.value = VideoState.Analyzing(0f)
            try {
                val result = stabilizationEngine.analyze(source, config, extractor) { progress ->
                    _videoState.postValue(VideoState.Analyzing(progress))
                }
                _videoState.value = VideoState.Stabilized(result)
            } catch (e: Exception) {
                _videoState.value = VideoState.Error("Stabilization failed: ${e.message}")
            }
        }
    }

    fun getFrameAt(timeMs: Long, applyStabilization: Boolean): Bitmap? {
        return null // Placeholder
    }

    fun requestPreviewFrame(timeMs: Long, callback: (Bitmap?, Bitmap?) -> Unit) {
        viewModelScope.launch {
            val original = frameExtractor?.extractFrameAt(timeMs)
            var stabilized: Bitmap? = null

            if (original != null && _videoState.value is VideoState.Stabilized) {
                stabilized = Bitmap.createBitmap(original.width, original.height, original.config)
                val fps = videoSource?.metadata?.fps ?: 30f
                val frameIndex = (timeMs / 1000f * fps).toInt()
                stabilizationEngine.getStabilizedFrame(original, frameIndex, stabilized)
            }

            callback(original, stabilized)
        }
    }

    fun exportVideo(outputFile: File) {
         val source = videoSource ?: return
         // Check if stabilized
         if (_videoState.value !is VideoState.Stabilized) return

         viewModelScope.launch {
             try {
                 val exporter = VideoExporter(getApplication())
                 exporter.export(source, outputFile, stabilizationEngine) { progress ->
                     // Handle progress (maybe separate Export State?)
                 }
                 // Handle success
             } catch (e: Exception) {
                 // Handle error
             }
         }
    }

    override fun onCleared() {
        super.onCleared()
        frameExtractor?.release()
        stabilizationEngine.release()
    }
}

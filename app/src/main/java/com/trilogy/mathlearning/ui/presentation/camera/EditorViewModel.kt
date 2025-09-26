package com.trilogy.mathlearning.ui.presentation.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor() : ViewModel() {
    var bmp: Bitmap? = null
        private set
    var rect: CropRect? = null
        private set
    var origin: ImageOrigin? = null
        private set
    var cropped: Bitmap? = null
        private set

    fun setInput(b: Bitmap, r: CropRect, o: ImageOrigin) {
        bmp = b
        rect = r
        origin = o
        cropped = null
    }

    fun setCropped(b: Bitmap) { cropped = b }
    fun reset() { bmp = null; rect = null; origin = null; cropped = null }
}

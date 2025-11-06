package com.trilogy.mathlearning.ui.presentation.solve_math

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trilogy.mathlearning.data.repository.BaiToan
import com.trilogy.mathlearning.data.repository.GenerateSolutionSteps
import com.trilogy.mathlearning.utils.CloudinaryUploader
import com.trilogy.mathlearning.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TakeMathViewModel @Inject constructor() : ViewModel() {

    // Danh sách ảnh (AI màn khác có thể cần >1)
    private val _images = MutableStateFlow<List<Bitmap>>(emptyList())
    val images: StateFlow<List<Bitmap>> = _images

    // URL sau khi upload (chỉ cần 1 cho màn cộng đồng)
    private val _imageUploadState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val imageUploadState: StateFlow<UiState<String>> = _imageUploadState

    private val _selectedItemSolveStyle = MutableStateFlow<SolveStyle>(SolveStyles.all.first())
    val selectedItemSolveStyle: StateFlow<SolveStyle> = _selectedItemSolveStyle

    private val _result = MutableStateFlow<UiState<BaiToan>>(UiState.Empty)
    val result: StateFlow<UiState<BaiToan>> = _result

    /** Thêm ảnh (giữ nguyên logic nhiều ảnh cho màn AI) */
    fun addImage(bmp: Bitmap) {
        _images.value = _images.value + bmp
    }

    /** Trường hợp màn cộng đồng: chỉ 1 ảnh → thay ảnh cũ & upload ngay */
    fun replaceWithAndUpload(bmp: Bitmap) {
        _images.value = listOf(bmp)
        uploadCurrentFirstImage()
    }

    /** Gọi khi muốn upload lại ảnh đầu tiên (retry) */
    fun uploadCurrentFirstImage() {
        val first = _images.value.firstOrNull() ?: run {
            _imageUploadState.value = UiState.Failure("No image selected")
            return
        }
        viewModelScope.launch {
            _imageUploadState.value = UiState.Loading
            val res = CloudinaryUploader.uploadBitmap(first)
            _imageUploadState.value = res
            if(res is UiState.Success) {
                //log url
                Log.i("TakeMathViewModel", "uploadCurrentFirstImage:" + res.data)
            }
        }
    }

    /** Xoá ảnh ở vị trí index; nếu xoá ảnh đầu thì reset state upload */
    fun removeAt(index: Int) {
        val list = _images.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _images.value = list
            if (index == 0) {
                _imageUploadState.value = UiState.Empty
            }
        }
    }

    fun setSelectedItemSolveStyle(solveStyle: SolveStyle) {
        _selectedItemSolveStyle.value = solveStyle
    }

    fun solve(images: List<Bitmap>) {
        viewModelScope.launch {
            _result.value = UiState.Loading
            val response = GenerateSolutionSteps.generateSteps(images)
            _result.value = response
        }
    }
}

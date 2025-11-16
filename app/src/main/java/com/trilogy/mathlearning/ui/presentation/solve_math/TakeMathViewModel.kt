package com.trilogy.mathlearning.ui.presentation.solve_math

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.trilogy.mathlearning.data.repository.BaiToan
import com.trilogy.mathlearning.data.repository.CommunityRepository
import com.trilogy.mathlearning.data.repository.GenerateSolutionSteps
import com.trilogy.mathlearning.utils.CloudinaryUploader
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TakeMathViewModel @Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _images = MutableStateFlow<List<Bitmap>>(emptyList())
    val images: StateFlow<List<Bitmap>> = _images

    private val _imageUploadState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val imageUploadState: StateFlow<UiState<String>> = _imageUploadState

    private val _selectedItemSolveStyle = MutableStateFlow(SolveStyles.all.first())
    val selectedItemSolveStyle: StateFlow<SolveStyle> = _selectedItemSolveStyle

    private val _result = MutableStateFlow<UiState<BaiToan>>(UiState.Empty)
    val result: StateFlow<UiState<BaiToan>> = _result

    fun addImage(bmp: Bitmap) {
        _images.value = _images.value + bmp
    }

    fun replaceWithAndUpload(bmp: Bitmap) {
        _images.value = listOf(bmp)
        uploadCurrentFirstImage()
    }

    fun uploadCurrentFirstImage() {
        val first = _images.value.firstOrNull() ?: run {
            _imageUploadState.value = UiState.Failure("No image selected")
            return
        }
        viewModelScope.launch {
            _imageUploadState.value = UiState.Loading
            val res = CloudinaryUploader.uploadBitmap(first)
            _imageUploadState.value = res
            if (res is UiState.Success) {
                Log.i("TakeMathViewModel", "uploadCurrentFirstImage: ${res.data}")
            }
        }
    }

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
        if (images.isEmpty()) {
            _result.value = UiState.Failure("Vui lòng thêm ít nhất một ảnh")
            return
        }

        viewModelScope.launch {
            _result.value = UiState.Loading

            val solveRes = GenerateSolutionSteps.generateSteps(images)

            when (val s = solveRes) {
                is UiState.Success -> {
                    val baiToan = s.data
                    postAiResultAndCreateQuestion(baiToan, images.first())
                    _result.value = UiState.Success(baiToan)
                }
                is UiState.Failure -> {
                    _result.value = UiState.Failure(s.error)
                }
                UiState.Empty -> {
                    _result.value = UiState.Empty
                }
                UiState.Loading -> { }
            }
        }
    }

    private suspend fun postAiResultAndCreateQuestion(
        baiToan: BaiToan,
        firstBitmap: Bitmap
    ) {
        val uploadRes = CloudinaryUploader.uploadBitmap(firstBitmap)
        _imageUploadState.value = uploadRes

        if (uploadRes !is UiState.Success) return

        val imageUrl = uploadRes.data
        try {
            when (val qRes = communityRepository.createQuestion(
                content = null,
                imageUrl = imageUrl
            )) {
                is NetworkResource.Success -> {
                    val question = qRes.data
                    val jsonContent = Gson().toJson(baiToan)
                    communityRepository.createAnswer(
                        questionId = question.id,
                        content = jsonContent,
                        imageUrl = null,
                        isAI = true
                    )
                }
                is NetworkResource.Error -> {
                    Log.e("TakeMathViewModel", "createQuestion error: ${qRes.message}")
                }
                is NetworkResource.NetworkException -> {
                    Log.e("TakeMathViewModel", "createQuestion network error: ${qRes.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("TakeMathViewModel", "post AI answer error: ${e.message}", e)
        }
    }

    fun resetResult() {
        _result.value = UiState.Empty
    }
}

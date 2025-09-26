package com.trilogy.mathlearning.ui.presentation.solve_math

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trilogy.mathlearning.data.repository.BaiToan
import com.trilogy.mathlearning.data.repository.GenerateSolutionSteps
import com.trilogy.mathlearning.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TakeMathViewModel @Inject constructor() : ViewModel() {

    private val _images = MutableStateFlow<List<Bitmap>>(emptyList())
    val images: StateFlow<List<Bitmap>> = _images

    private val _selectedItemSolveStyle = MutableStateFlow<SolveStyle>(SolveStyles.all.first())
    val selectedItemSolveStyle: StateFlow<SolveStyle> = _selectedItemSolveStyle

    private val _result = MutableStateFlow<UiState<BaiToan>>(UiState.Empty)
    val result: StateFlow<UiState<BaiToan>> = _result

    fun addImage(bmp: Bitmap) {
        _images.value = _images.value + bmp
    }

    fun solve(images: List<Bitmap>) {
        viewModelScope.launch {
            _result.value = UiState.Loading
            val response = GenerateSolutionSteps.generateSteps(images)
            _result.value = response
        }
    }

    fun setSelectedItemSolveStyle(solveStyle: SolveStyle) {
        _selectedItemSolveStyle.value = solveStyle
    }

    fun removeAt(index: Int) {
        _images.value = _images.value.toMutableList().also { it.removeAt(index) }
    }
}

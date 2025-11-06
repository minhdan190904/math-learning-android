package com.trilogy.mathlearning.ui.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trilogy.mathlearning.data.repository.CommunityRepository
import com.trilogy.mathlearning.domain.model.QuestionResDto
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val repo: CommunityRepository
) : ViewModel() {

    /* ---------- TẠO QUESTION ---------- */
    private val _postState = MutableStateFlow<UiState<QuestionResDto>>(UiState.Empty)
    val postState: StateFlow<UiState<QuestionResDto>> = _postState

    fun createQuestion(content: String?, imageUrl: String?) {
        if (content.isNullOrBlank() && imageUrl.isNullOrBlank()) {
            _postState.value = UiState.Failure("Vui lòng nhập nội dung hoặc đính kèm ảnh")
            return
        }
        viewModelScope.launch {
            _postState.value = UiState.Loading
            when (val res = repo.createQuestion(content, imageUrl)) {
                is NetworkResource.Success -> _postState.value = UiState.Success(res.data)
                is NetworkResource.Error -> _postState.value = UiState.Failure(res.message)
                is NetworkResource.NetworkException -> _postState.value = UiState.Failure(res.message)
            }
        }
    }
    fun resetPostState() { _postState.value = UiState.Empty }

    /* ---------- LIST QUESTIONS ---------- */
    private val _questionsState = MutableStateFlow<UiState<List<QuestionResDto>>>(UiState.Empty)
    val questionsState: StateFlow<UiState<List<QuestionResDto>>> = _questionsState

    fun loadQuestions() = viewModelScope.launch {
        _questionsState.value = UiState.Loading
        when (val res = repo.loadQuestions()) {
            is NetworkResource.Success -> _questionsState.value = UiState.Success(res.data)
            is NetworkResource.Error -> _questionsState.value = UiState.Failure(res.message)
            is NetworkResource.NetworkException -> _questionsState.value = UiState.Failure(res.message)
        }
    }


    /* ---------- QUESTION DETAIL ---------- */
    private val _detailState = MutableStateFlow<UiState<QuestionResDto>>(UiState.Empty)
    val detailState: StateFlow<UiState<QuestionResDto>> = _detailState

    fun loadQuestionDetail(id: String) = viewModelScope.launch {
        _detailState.value = UiState.Loading
        when (val res = repo.loadQuestionDetail(id)) {
            is NetworkResource.Success -> _detailState.value = UiState.Success(res.data)
            is NetworkResource.Error -> _detailState.value = UiState.Failure(res.message)
            is NetworkResource.NetworkException -> _detailState.value = UiState.Failure(res.message)
        }
    }

    fun createAnswer(questionId: String, content: String?, imageUrl: String?) = viewModelScope.launch {
        when (repo.createAnswer(questionId, content, imageUrl)) {
            is NetworkResource.Success -> loadQuestionDetail(questionId) // gửi xong reload detail
            else -> {}
        }
    }

    fun toggleLike(answerId: String, questionId: String) = viewModelScope.launch {
        when (repo.toggleLike(answerId)) {
            is NetworkResource.Success -> loadQuestionDetail(questionId) // like/unlike xong reload
            else -> {}
        }
    }
}

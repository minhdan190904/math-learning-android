package com.trilogy.mathlearning.ui.presentation.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trilogy.mathlearning.data.repository.StatisticRepository
import com.trilogy.mathlearning.domain.model.StatisticLikeResDto
import com.trilogy.mathlearning.domain.model.StatisticResDto
import com.trilogy.mathlearning.domain.model.UserProgressResDto
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val repo: StatisticRepository
) : ViewModel() {

    private val _pointLeaderboardState =
        MutableStateFlow<UiState<StatisticResDto>>(UiState.Empty)
    val pointLeaderboardState: StateFlow<UiState<StatisticResDto>> =
        _pointLeaderboardState

    private val _likeLeaderboardState =
        MutableStateFlow<UiState<StatisticLikeResDto>>(UiState.Empty)
    val likeLeaderboardState: StateFlow<UiState<StatisticLikeResDto>> =
        _likeLeaderboardState

    private val _progressState =
        MutableStateFlow<UiState<UserProgressResDto>>(UiState.Empty)
    val progressState: StateFlow<UiState<UserProgressResDto>> =
        _progressState

    fun loadTopUsersByPoints() = viewModelScope.launch {
        _pointLeaderboardState.value = UiState.Loading
        when (val res = repo.getTopUsersByPoints()) {
            is NetworkResource.Success ->
                _pointLeaderboardState.value = UiState.Success(res.data)

            is NetworkResource.Error ->
                _pointLeaderboardState.value = UiState.Failure(res.message)

            is NetworkResource.NetworkException ->
                _pointLeaderboardState.value = UiState.Failure(res.message)
        }
    }

    fun loadTopUsersByLikes() = viewModelScope.launch {
        _likeLeaderboardState.value = UiState.Loading
        when (val res = repo.getTopUsersByLikes()) {
            is NetworkResource.Success ->
                _likeLeaderboardState.value = UiState.Success(res.data)

            is NetworkResource.Error ->
                _likeLeaderboardState.value = UiState.Failure(res.message)

            is NetworkResource.NetworkException ->
                _likeLeaderboardState.value = UiState.Failure(res.message)
        }
    }

    fun loadUserProgress() = viewModelScope.launch {
        _progressState.value = UiState.Loading
        when (val res = repo.getUserProgress()) {
            is NetworkResource.Success ->
                _progressState.value = UiState.Success(res.data)

            is NetworkResource.Error ->
                _progressState.value = UiState.Failure(res.message)

            is NetworkResource.NetworkException ->
                _progressState.value = UiState.Failure(res.message)
        }
    }

    fun resetPointLeaderboard() {
        _pointLeaderboardState.value = UiState.Empty
    }

    fun resetLikeLeaderboard() {
        _likeLeaderboardState.value = UiState.Empty
    }

    fun resetProgress() {
        _progressState.value = UiState.Empty
    }
}

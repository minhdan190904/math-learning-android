package com.trilogy.mathlearning.ui.presentation.math

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trilogy.mathlearning.data.repository.PracticeRepository
import com.trilogy.mathlearning.domain.model.ExerciseResDto
import com.trilogy.mathlearning.domain.model.MathConfigResDto
import com.trilogy.mathlearning.domain.model.PracticeResDto
import com.trilogy.mathlearning.domain.model.SubmitAnswerReqDto
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PracticeFlowViewModel @Inject constructor(
    private val repo: PracticeRepository
) : ViewModel() {

    companion object {
        private const val TAG = "PracticeFlowVM"
        private const val DEFAULT_DURATION_MIN = 45
        private val LABELS = listOf("A", "B", "C", "D", "E", "F")
    }

    private val _configState = MutableStateFlow<UiState<MathConfigResDto>>(UiState.Empty)
    val configState: StateFlow<UiState<MathConfigResDto>> = _configState

    fun loadConfig() = viewModelScope.launch {
        _configState.value = UiState.Loading
        when (val r = repo.getMathConfig()) {
            is NetworkResource.Success -> _configState.value = UiState.Success(r.data)
            is NetworkResource.Error -> _configState.value = UiState.Failure(r.message)
            is NetworkResource.NetworkException -> _configState.value = UiState.Failure(r.message)
        }
    }

    private val _createState = MutableStateFlow<UiState<PracticeResDto>>(UiState.Empty)
    val createState: StateFlow<UiState<PracticeResDto>> = _createState

    private val _exercisesState = MutableStateFlow<UiState<List<ExerciseResDto>>>(UiState.Empty)
    val exercisesState: StateFlow<UiState<List<ExerciseResDto>>> = _exercisesState

    private val _submitState = MutableStateFlow<UiState<PracticeResDto>>(UiState.Empty)
    val submitState: StateFlow<UiState<PracticeResDto>> = _submitState

    private val _examUi = MutableStateFlow<Exam?>(null)
    val examUi: StateFlow<Exam?> = _examUi

    val selectedAnswers = mutableMapOf<String, Int>()
    var timeSpentSec: Int = 0
        private set
    private var timerJob: Job? = null

    fun clearCreateState() { _createState.value = UiState.Empty }
    fun clearExercisesState() { _exercisesState.value = UiState.Empty }
    fun clearSubmitState() { _submitState.value = UiState.Empty }
    fun clearExamUi() { _examUi.value = null }

    fun resetAllForNewPractice() {
        _createState.value = UiState.Empty
        _exercisesState.value = UiState.Empty
        _submitState.value = UiState.Empty
        _examUi.value = null
        selectedAnswers.clear()
        stopTimer()
        timeSpentSec = 0
    }

    fun createPractice(grade: Int, chapterId: Int?, examType: String?) = viewModelScope.launch {
        _createState.value = UiState.Loading
        when (val r = repo.createPractice(grade, chapterId, examType)) {
            is NetworkResource.Success -> _createState.value = UiState.Success(r.data)
            is NetworkResource.Error -> _createState.value = UiState.Failure(r.message)
            is NetworkResource.NetworkException -> _createState.value = UiState.Failure(r.message)
        }
    }

    fun loadExercisesAndBuildExam(practiceId: String, examTitle: String = "Bài luyện tập") =
        viewModelScope.launch {
            _exercisesState.value = UiState.Loading
            when (val r = repo.getExercises(practiceId)) {
                is NetworkResource.Success -> {
                    _exercisesState.value = UiState.Success(r.data)
                    _examUi.value = r.data.toExam(examTitle)
                    selectedAnswers.clear()
                    timeSpentSec = 0
                }
                is NetworkResource.Error -> _exercisesState.value = UiState.Failure(r.message)
                is NetworkResource.NetworkException -> _exercisesState.value = UiState.Failure(r.message)
            }
        }

    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                timeSpentSec++
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun select(exerciseId: String, index: Int) {
        selectedAnswers[exerciseId] = index
    }

    fun submit(practiceId: String) = viewModelScope.launch {
        val answers = selectedAnswers.map { (id, pickIndex) -> SubmitAnswerReqDto(id, pickIndex) }
        _submitState.value = UiState.Loading
        when (val r = repo.submitPractice(practiceId, timeSpentSec, answers)) {
            is NetworkResource.Success -> _submitState.value = UiState.Success(r.data)
            is NetworkResource.Error -> _submitState.value = UiState.Failure(r.message)
            is NetworkResource.NetworkException -> _submitState.value = UiState.Failure(r.message)
        }
    }

    private fun List<ExerciseResDto>.toExam(title: String): Exam {
        val qs = this.mapIndexed { idx, ex ->
            Question(
                id = ex.id,
                index = idx + 1,
                content = ex.problem,
                choices = ex.choices.mapIndexed { cidx, text ->
                    Choice(
                        label = LABELS.getOrElse(cidx) { (65 + cidx).toChar().toString() },
                        text = text
                    )
                },
                answer = null
            )
        }
        return Exam(
            id = "practice",
            title = title,
            durationMin = DEFAULT_DURATION_MIN,
            questions = qs
        )
    }
}

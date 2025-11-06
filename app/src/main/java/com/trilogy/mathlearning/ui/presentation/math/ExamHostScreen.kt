package com.trilogy.mathlearning.ui.presentation.math

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.trilogy.mathlearning.utils.UiState

@Composable
fun ExamHostScreen(
    practiceId: String,
    vm: PracticeFlowViewModel,
    onBack: () -> Unit,
    onSubmitted: (PracticeResult: String) -> Unit
) {
    val exam by vm.examUi.collectAsState()
    val exState by vm.exercisesState.collectAsState()
    val submit by vm.submitState.collectAsState()
    var showExit by remember { mutableStateOf(false) }

    LaunchedEffect(practiceId) {
        vm.clearExercisesState()
        vm.clearExamUi()
        vm.loadExercisesAndBuildExam(practiceId)
        vm.startTimer()
    }

    DisposableEffect(Unit) {
        onDispose {
            vm.stopTimer()
            vm.clearExercisesState()
            vm.clearExamUi()
        }
    }

    BackHandler { showExit = true }

    when (val s = exState) {
        UiState.Loading, UiState.Empty -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        is UiState.Failure -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(s.error ?: "Lỗi tải bài") }
        is UiState.Success -> {
            val examUi = exam ?: return
            ExamTakingScreen(
                exam = examUi,
                onBack = { showExit = true },
                onSubmit = { answersMap ->
                    val toIndex: (String) -> Int = { label ->
                        ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(label.uppercase())).let { if (it < 0) 0 else it }
                    }
                    vm.selectedAnswers.clear()
                    answersMap.forEach { (qid, label) -> vm.select(qid, toIndex(label)) }
                    vm.submit(practiceId)
                }
            )
        }
    }

    LaunchedEffect(submit) {
        val ok = (submit as? UiState.Success)?.data ?: return@LaunchedEffect
        vm.stopTimer()
        onSubmitted(ok.practiceId)
    }

    if (showExit) {
        AlertDialog(
            onDismissRequest = { showExit = false },
            title = { Text("Thoát bài?") },
            text = { Text("Bạn có muốn thoát khỏi bài đang làm?") },
            confirmButton = {
                TextButton(onClick = {
                    showExit = false
                    vm.resetAllForNewPractice()
                    onBack()
                }) { Text("Thoát") }
            },
            dismissButton = {
                TextButton(onClick = { showExit = false }) { Text("Hủy") }
            }
        )
    }
}

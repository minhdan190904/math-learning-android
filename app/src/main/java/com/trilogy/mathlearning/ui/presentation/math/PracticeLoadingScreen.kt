package com.trilogy.mathlearning.ui.presentation.math

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.trilogy.mathlearning.utils.UiState
import kotlinx.coroutines.delay

@Composable
fun PracticeLoadingScreen(
    grade: Int,
    chapterId: Int?,
    examType: String?,
    vm: PracticeFlowViewModel,
    onReady: (String) -> Unit,
    onCancel: () -> Unit
) {
    val create by vm.createState.collectAsState()
    var minDelayDone by remember { mutableStateOf(false) }

    LaunchedEffect(grade, chapterId, examType) {
        vm.resetAllForNewPractice()
        vm.createPractice(grade, chapterId, examType)
        delay(3000)
        minDelayDone = true
    }

    BackHandler {
        vm.resetAllForNewPractice()
        onCancel()
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("bookstack.json"))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(220.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text("Đang tải bài tập", style = MaterialTheme.typography.titleMedium)
        }
    }

    LaunchedEffect(create, minDelayDone) {
        val d = (create as? UiState.Success)?.data
        if (d != null && minDelayDone) onReady(d.practiceId)
    }
}

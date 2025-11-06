package com.trilogy.mathlearning.ui.presentation.math

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trilogy.mathlearning.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeHomeScreen(
    vm: PracticeFlowViewModel,
    onPickGrade: (Int) -> Unit,
    onOpenHistory: () -> Unit
) {
    val state by vm.configState.collectAsState()
    LaunchedEffect(Unit) {
        if (state is UiState.Empty) vm.loadConfig()
        vm.clearCreateState()
        vm.clearSubmitState()
        vm.clearExercisesState()
        vm.clearExamUi()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Luyện tập") },
                actions = { TextButton(onClick = onOpenHistory) { Text("Lịch sử") } }
            )
        }
    ) { inner ->
        when (val s = state) {
            UiState.Loading, UiState.Empty -> Box(
                Modifier.fillMaxSize().padding(inner),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is UiState.Failure -> Box(
                Modifier.fillMaxSize().padding(inner),
                contentAlignment = Alignment.Center
            ) { Text(s.error ?: "Lỗi tải cấu hình") }

            is UiState.Success -> {
                val grades = s.data.curriculum
                Column(Modifier.fillMaxSize().padding(inner).padding(16.dp)) {
                    grades.forEach {
                        ListItem(
                            headlineContent = { Text(it.title) },
                            supportingContent = { Text("Gồm ${it.chapters.size} chương") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingContent = {
                                TextButton(onClick = { onPickGrade(it.grade) }) { Text("Chọn") }
                            }
                        )
                        HorizontalDivider(
                            Modifier,
                            DividerDefaults.Thickness,
                            DividerDefaults.color
                        )
                    }
                }
            }
        }
    }
}

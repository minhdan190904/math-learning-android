package com.trilogy.mathlearning.ui.presentation.math

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trilogy.mathlearning.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterPickerScreen(
    grade: Int,
    vm: PracticeFlowViewModel,
    onStartPractice: (practiceId: String) -> Unit,
    onBack: () -> Unit,
    onStartLoading: (Int, Int) -> Unit
) {
    val config by vm.configState.collectAsState()

    LaunchedEffect(grade) {
        vm.clearCreateState()
        vm.clearSubmitState()
        vm.clearExercisesState()
        vm.clearExamUi()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chọn chương - Lớp $grade") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null) } }
            )
        }
    ) { inner ->
        when (val s = config) {
            UiState.Loading, UiState.Empty -> Box(Modifier.fillMaxSize().padding(inner), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
            is UiState.Failure -> Box(Modifier.fillMaxSize().padding(inner), contentAlignment = androidx.compose.ui.Alignment.Center) { Text(s.error ?: "Lỗi") }
            is UiState.Success -> {
                val chapters = s.data.curriculum.firstOrNull { it.grade == grade }?.chapters.orEmpty()
                Column(Modifier.fillMaxSize().padding(inner)) {
                    chapters.forEach { c ->
                        ListItem(
                            headlineContent = { Text(c.title) },
                            supportingContent = { Text(c.description ?: "") },
                            trailingContent = {
                                Button(onClick = { onStartLoading(grade, c.id) }) {
                                    Text("Bắt đầu")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

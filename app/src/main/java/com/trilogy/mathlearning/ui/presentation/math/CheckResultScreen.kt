package com.trilogy.mathlearning.ui.presentation.math

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trilogy.mathlearning.MathInlineSentence
import com.trilogy.mathlearning.R
import com.trilogy.mathlearning.domain.model.ExerciseResDto
import com.trilogy.mathlearning.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckResultScreen(
    practiceId: String,
    vm: PracticeFlowViewModel,
    onBack: () -> Unit
) {
    val exState by vm.exercisesState.collectAsState()
    var index by remember(practiceId) { mutableStateOf(0) }
    var showExit by remember { mutableStateOf(false) }

    LaunchedEffect(practiceId) {
        vm.clearExercisesState()
        vm.clearExamUi()
        vm.loadExercisesAndBuildExam(practiceId)
    }

    BackHandler { showExit = true }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = { Text("Xem kết quả", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { showExit = true }) {
                        Icon(painterResource(R.drawable.ic_back), null)
                    }
                }
            )
        },
        bottomBar = {
            val list = (exState as? UiState.Success)?.data.orEmpty()
            if (list.isNotEmpty()) {
                NumberBarReview(
                    total = list.size,
                    current = index,
                    answeredIds = vm.selectedAnswers.keys.toSet(),
                    ids = list.map { it.id },
                    onJump = { index = it }
                )
            }
        },
        containerColor = Color.White
    ) { inner ->
        when (val s = exState) {
            UiState.Loading, UiState.Empty -> Box(
                Modifier.fillMaxSize().padding(inner),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            is UiState.Failure -> Box(
                Modifier.fillMaxSize().padding(inner),
                contentAlignment = Alignment.Center
            ) { Text(s.error ?: "Lỗi tải bài") }
            is UiState.Success -> {
                val list = s.data
                val ex = list.getOrNull(index) ?: return@Scaffold
                ReviewQuestionBlock(
                    index = index + 1,
                    ex = ex,
                    pickedIndex = vm.selectedAnswers[ex.id],
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize()
                )
            }
        }
    }

    if (showExit) {
        AlertDialog(
            onDismissRequest = { showExit = false },
            title = { Text("Thoát xem kết quả?") },
            text = { Text("Quay lại màn trước.") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showExit = false
                    onBack()
                }) { Text("Thoát") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showExit = false }) { Text("Hủy") }
            }
        )
    }
}

@Composable
private fun ReviewQuestionBlock(
    index: Int,
    ex: ExerciseResDto,
    pickedIndex: Int?,
    modifier: Modifier = Modifier
) {
    val correctIndex = ex.result
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Câu $index", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("|  #${ex.id}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        MathInlineSentence(raw = ex.problem)

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ex.choices.forEachIndexed { cidx, text ->
                val isPicked = pickedIndex == cidx
                val isCorrect = correctIndex == cidx
                val shape = examShapes().large
                val borderColor =
                    when {
                        isCorrect -> Color(0xFF2DBE60)
                        isPicked && !isCorrect -> Color(0xFFE45858)
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    }
                val label = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ").getOrNull(cidx)?.toString() ?: (cidx + 1).toString()

                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        color = Color.White,
                        shape = shape,
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp,
                        border = androidx.compose.foundation.BorderStroke(if (isPicked || isCorrect) 2.dp else 1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(examShapes().small)
                                    .background(
                                        when {
                                            isCorrect -> Color(0xFF2DBE60)
                                            isPicked && !isCorrect -> Color(0xFFE45858)
                                            else -> ChoiceGray
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(Modifier.width(12.dp))
                            MathInlineSentence(raw = text, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        Text("Lời giải", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        MathInlineSentence(raw = ex.solution)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun NumberBarReview(
    total: Int,
    current: Int,
    answeredIds: Set<String>,
    ids: List<String>,
    onJump: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items((0 until total).toList()) { i ->
            val selected = i == current
            val answered = ids.getOrNull(i)?.let { it in answeredIds } == true
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when {
                            selected -> Color(0x112D4EA3)
                            answered -> Color(0x112DBE60)
                            else -> Color(0x0F000000)
                        }
                    )
                    .border(
                        2.dp,
                        if (selected) Navy else Color(0x22000000),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onJump(i) },
                contentAlignment = Alignment.Center
            ) {
                Text("${i + 1}", color = if (selected) Navy else MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

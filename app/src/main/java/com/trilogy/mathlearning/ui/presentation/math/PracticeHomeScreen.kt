package com.trilogy.mathlearning.ui.presentation.math

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
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
                actions = {
                    IconButton(onClick = onOpenHistory) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "Lịch sử luyện tập"
                        )
                    }
                }
            )
        }
    ) { inner ->
        when (val s = state) {
            UiState.Loading, UiState.Empty -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is UiState.Failure -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentAlignment = Alignment.Center
            ) {
                Text(s.error ?: "Lỗi tải cấu hình")
            }

            is UiState.Success -> {
                val grades = s.data.curriculum
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(grades) { gradeCfg ->
                        GradeCard(
                            title = gradeCfg.title,
                            grade = gradeCfg.grade,
                            chapterCount = gradeCfg.chapters.size,
                            onClick = { onPickGrade(gradeCfg.grade) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GradeCard(
    title: String,
    grade: Int,
    chapterCount: Int,
    onClick: () -> Unit
) {
    val bluePrimary = Color(0xFF1565C0)
    val blueLight = Color(0xFF42A5F5)

    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )

    val borderGradient = Brush.horizontalGradient(
        colors = listOf(
            bluePrimary.copy(alpha = 0.7f),
            blueLight.copy(alpha = 0.7f)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                onClick = {
                    pressed = true
                    onClick()
                    pressed = false
                }
            ),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(borderGradient)
                .padding(1.dp)
                .clip(MaterialTheme.shapes.large)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    bluePrimary,
                                    blueLight
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "K$grade",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "$chapterCount chương",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.width(12.dp))

                FilledTonalButton(
                    onClick = onClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = bluePrimary,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Chọn")
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

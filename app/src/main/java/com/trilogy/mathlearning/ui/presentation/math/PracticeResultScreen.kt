package com.trilogy.mathlearning.ui.presentation.math

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.trilogy.mathlearning.R
import com.trilogy.mathlearning.domain.model.PracticeResDto
import com.trilogy.mathlearning.utils.UiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeResultScreen(
    vm: PracticeFlowViewModel,
    onViewDetail: (String) -> Unit,
    onBackToChapters: () -> Unit
) {
    val submit by vm.submitState.collectAsState()
    var data by remember { mutableStateOf<PracticeResDto?>(null) }
    var playOnce by remember { mutableStateOf(true) }

    LaunchedEffect(submit) {
        val d = (submit as? UiState.Success)?.data ?: return@LaunchedEffect
        if (data == null) {
            data = d
            vm.clearSubmitState()
            vm.clearExercisesState()
            vm.clearExamUi()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFEAF2FF),
                        Color(0xFFD6E6FF),
                        Color(0xFFBFD7FF)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            topBar = {
                TopAppBar(
                    title = { Text("Kết quả") },
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = Color(0xFF0A3D91)
                    )
                )
            }
        ) { inner ->
            Box(Modifier.fillMaxSize().padding(inner)) {
                if (playOnce) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("congratulation.json"))
                    val progress by animateLottieCompositionAsState(composition = composition, iterations = 1)
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .align(Alignment.TopCenter)
                    )
                    LaunchedEffect(progress) { if (progress >= 1f) playOnce = false }
                }

                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.Top)
                ) {
                    Spacer(Modifier.height(8.dp))
                    Image(
                        painter = painterResource(R.drawable.img_cup),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Fit
                    )

                    val res = data
                    if (res == null) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Đang tải kết quả...", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF0A3D91))
                        }
                    } else {
                        val scoreDisplay = formatScore(res.score)
                        Text(
                            "Điểm: $scoreDisplay/10",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0A3D91)
                        )

                        ScoreDonutBlue(
                            percent = if (res.totalAnswers == 0) 0f else res.correctAnswers.toFloat() / res.totalAnswers.toFloat(),
                            scoreText = "${res.score}",
                            subText = "Đúng: ${res.correctAnswers}/${res.totalAnswers}"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoCardBlue(
                                title = "Đúng",
                                value = "${res.correctAnswers}/${res.totalAnswers}"
                            )
                            InfoCardBlue(
                                title = "Thời gian",
                                value = formatTime(res.timeSpent)
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(top = 6.dp, bottom = 2.dp),
                            thickness = 1.dp,
                            color = Color(0x1A0A3D91)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Button(
                                onClick = { onViewDetail(res.practiceId) },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C6EF2)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                            ) {
                                Text("Xem kết quả")
                            }
                            OutlinedButton(
                                onClick = {
                                    vm.resetAllForNewPractice()
                                    onBackToChapters()
                                },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1C6EF2))
                            ) {
                                Text("Về chương")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatScore(score: Int): String {
    val value = score.toDouble() / 10.0
    return String.format(Locale.getDefault(), "%.1f", value)
}

@Composable
private fun ScoreDonutBlue(percent: Float, scoreText: String, subText: String) {
    Box(
        modifier = Modifier
            .size(190.dp)
            .drawBehind {
                val stroke = size.minDimension * 0.12f
                drawArc(
                    color = Color(0x221C6EF2),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.sweepGradient(listOf(Color(0xFF1C6EF2), Color(0xFF0A3D91), Color(0xFF1C6EF2))),
                    startAngle = -90f,
                    sweepAngle = 360f * percent.coerceIn(0f, 1f),
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(scoreText, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color(0xFF0A3D91))
            Spacer(Modifier.height(2.dp))
            Text(subText, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2E64C5))
        }
    }
}

@Composable
private fun RowScope.InfoCardBlue(title: String, value: String) {
    Column(
        modifier = Modifier
            .weight(1f)
            .heightIn(min = 78.dp)
            .background(Color(0x141C6EF2), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2E64C5))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF0A3D91))
    }
}

private fun formatTime(s: Int): String {
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, sec) else "%02d:%02d".format(m, sec)
}

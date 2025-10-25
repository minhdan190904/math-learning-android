package com.trilogy.mathlearning.ui.presentation.math

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trilogy.mathlearning.MathInlineSentence
import com.trilogy.mathlearning.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamTakingScreen(
    exam: Exam,
    onBack: () -> Unit,
    onSubmit: (answers: Map<String, String>) -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }
    var seconds by remember(exam.id) { mutableIntStateOf(exam.durationMin * 60) }
    val answers = remember { mutableStateMapOf<String, String>() }
    var index by remember { mutableStateOf(0) }

    // đếm ngược
    LaunchedEffect(exam.id) {
        while (seconds > 0) { delay(1000); seconds-- }
        showConfirm = true
    }

    Scaffold(
        topBar = {
            TopAppBarTimer(
                text = formatTime(seconds),
                onBack = onBack,
                onSubmit = { showConfirm = true }
            )
        },
        bottomBar = {
            Column(Modifier.navigationBarsPadding()) {
                SkipButton(onClick = { /* TODO: mark skipped */ })
                NumberBar(
                    total = exam.questions.size,
                    current = index,
                    answers = answers,
                    onJump = { index = it }
                )
            }
        },
        containerColor = Color.White
    ) { inner ->
        val q = exam.questions[index]
        QuestionBlock(
            q = q,
            chosen = answers[q.id],
            onChoose = { answers[q.id] = it },
            modifier = Modifier.padding(inner).fillMaxSize()
        )
    }

    if (showConfirm) {
        SubmitDialogBig(
            title = exam.title,
            onDismiss = { showConfirm = false },
            onConfirm = { onSubmit(answers) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarTimer(text: String, onBack: () -> Unit, onSubmit: () -> Unit) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Navy,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = { Text(text, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(painterResource(R.drawable.ic_back), null, tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        actions = {
            TextButton(onClick = onSubmit) {
                Text("Nộp bài", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
private fun QuestionBlock(
    q: Question,
    chosen: String?,
    onChoose: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header câu + mã giống ảnh
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Câu ${q.index}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("|  #${q.id}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.weight(1f))
            Icon(painterResource(R.drawable.ic_report), contentDescription = "Báo lỗi",
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Icon(painterResource(R.drawable.ic_bookmark), contentDescription = "Đánh dấu",
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Nội dung (MathInlineSentence của bạn)
        MathInlineSentence(raw = q.content)

        // Lựa chọn A/B/C/D
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            q.choices.forEach { c ->
                val selected = c.label == chosen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(examShapes().large)
                        .background(if (selected) MaterialTheme.colorScheme.primary.copy(.08f) else Color.Transparent)
                        .clickable { onChoose(c.label) }
                        .padding(vertical = 12.dp, horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // vòng tròn A/B/C/D
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(examShapes().small)
                            .background(if (selected) MaterialTheme.colorScheme.primary else ChoiceGray),
                        contentAlignment = Alignment.Center
                    ) { Text(c.label, color = Color.White, fontWeight = FontWeight.SemiBold) }

                    Spacer(Modifier.width(12.dp))
                    val content = c.tex?.let { "\$${it}\$" } ?: (c.text ?: "")
                    MathInlineSentence(raw = content, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SkipButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(28.dp))
            .border(1.dp, Color(0x33000000), RoundedCornerShape(28.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("BỎ QUA", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun NumberBar(total: Int, current: Int, answers: Map<String, String>, onJump: (Int) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items((0 until total).toList()) { i ->
            val selected = i == current
            val answered = answers.values.elementAtOrNull(i) != null
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

private fun formatTime(s: Int): String {
    val h = s / 3600; val m = (s % 3600) / 60; val sec = s % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, sec) else "%02d:%02d".format(m, sec)
}

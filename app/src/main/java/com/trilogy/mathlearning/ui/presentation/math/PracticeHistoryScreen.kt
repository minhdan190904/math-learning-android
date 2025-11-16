package com.trilogy.mathlearning.ui.presentation.math

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trilogy.mathlearning.domain.model.PracticeResDto
import com.trilogy.mathlearning.domain.model.PracticeStatus
import com.trilogy.mathlearning.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeHistoryScreen(
    vm: PracticeFlowViewModel,
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit
) {
    val historyState by vm.historyState.collectAsState()

    LaunchedEffect(Unit) {
        if (historyState is UiState.Empty) {
            vm.loadHistory()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lịch sử luyện tập") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { inner ->
        when (val s = historyState) {
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
                Text(s.error ?: "Lỗi tải lịch sử")
            }

            is UiState.Success -> {
                val practices = s.data.practices
                if (practices.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(inner),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Chưa có bài luyện tập nào")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(inner)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(practices) { p ->
                            PracticeHistoryItem(
                                practice = p,
                                onClick = { onOpenDetail(p.practiceId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeHistoryItem(
    practice: PracticeResDto,
    onClick: () -> Unit
) {
    val bluePrimary = Color(0xFF1565C0)
    val blueLight = Color(0xFF42A5F5)

    val borderGradient = Brush.horizontalGradient(
        colors = listOf(
            bluePrimary.copy(alpha = 0.7f),
            blueLight.copy(alpha = 0.7f)
        )
    )

    val minutes = practice.timeSpent / 60
    val seconds = practice.timeSpent % 60

    // an toàn nếu server trả null
    val safeStatus = practice.status ?: PracticeStatus.IN_PROGRESS

    val statusText = when (safeStatus) {
        PracticeStatus.IN_PROGRESS -> "Đang làm"
        PracticeStatus.COMPLETED -> "Hoàn thành"
        PracticeStatus.ABANDONED -> "Bỏ dở"
    }

    val statusColor = when (safeStatus) {
        PracticeStatus.IN_PROGRESS -> bluePrimary
        PracticeStatus.COMPLETED -> Color(0xFF2E7D32)
        PracticeStatus.ABANDONED -> Color(0xFFF57C00)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(borderGradient)
                .padding(1.dp)
                .clip(MaterialTheme.shapes.large)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(bluePrimary.copy(alpha = 0.08f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Lớp ${practice.grade}" +
                                    (practice.chapterId?.let { " • Chương $it" } ?: ""),
                            style = MaterialTheme.typography.labelMedium,
                            color = bluePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Điểm: ${practice.score}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Đúng ${practice.correctAnswers}/${practice.totalAnswers}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Thời gian: ${minutes}p${seconds}s",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(10.dp))

                Divider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Nhấn để xem chi tiết bài làm",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

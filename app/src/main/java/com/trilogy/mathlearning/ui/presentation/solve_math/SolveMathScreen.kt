package com.trilogy.mathlearning.ui.presentation.solve_math

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.trilogy.mathlearning.domain.model.AnswerResDto
import com.trilogy.mathlearning.domain.model.QuestionResDto
import com.trilogy.mathlearning.ui.presentation.community.CommunityViewModel
import com.trilogy.mathlearning.ui.presentation.navigation.Screen
import com.trilogy.mathlearning.utils.UiState
import com.trilogy.mathlearning.utils.myUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolveMathScreen(
    navControllerApp: NavController,      // graph ngoài (TakeMathImage)
    navControllerInner: NavController     // graph trong HomeRoot (solve-history)
) {
    val vm: CommunityViewModel = hiltViewModel()
    val state by vm.questionsState.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadQuestions()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.History, contentDescription = null, tint = Color(0xFF1677FF))
                        Spacer(Modifier.width(8.dp))
                        Text("Lịch sử giải toán AI", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                ElevatedButton(
                    onClick = { navControllerApp.navigate(Screen.TakeMathImage.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Giải bài toán mới", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        containerColor = Color(0xFFF6F7FB)
    ) { inner ->
        when (val s = state) {
            is UiState.Loading, UiState.Empty -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(5) {
                        HistorySkeletonCard()
                    }
                }
            }

            is UiState.Failure -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner),
                    contentAlignment = Alignment.Center
                ) {
                    Text(s.error ?: "Lỗi tải lịch sử bài toán")
                }
            }

            is UiState.Success -> {
                val allQuestions = s.data
                val aiQuestions = allQuestions
                    .mapNotNull { q ->
                        val aiAnswer = q.answers.firstOrNull { it.isAI && it.authorEmail == myUser!!.email }
                        if (aiAnswer != null) AiHistoryItem(question = q, aiAnswer = aiAnswer) else null
                    }

                if (aiQuestions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(inner),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Chưa có bài toán nào được giải bằng AI",
                                color = Color(0xFF9095A0)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Nhấn \"Giải bài toán mới\" để bắt đầu",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9095A0)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(inner),
                        contentPadding = PaddingValues(bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(aiQuestions, key = { it.question.id }) { item ->
                            AiHistoryCard(
                                item = item,
                                onClick = {
                                    navControllerInner.navigate("solve-history/${item.question.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class AiHistoryItem(
    val question: QuestionResDto,
    val aiAnswer: AnswerResDto
)

@Composable
private fun AiHistoryCard(
    item: AiHistoryItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            if (!item.question.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = item.question.imageUrl,
                    contentDescription = "Ảnh bài toán",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
            }

            Text(
                text = item.question.topic.ifBlank { "Bài toán AI" },
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Nhấn để xem lời giải chi tiết",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9095A0)
            )
        }
    }
}

@Composable
private fun HistorySkeletonCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(10.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
            )
        }
    }
}

package com.trilogy.mathlearning.ui.presentation.solve_math

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.trilogy.mathlearning.MathInlineSentence
import com.trilogy.mathlearning.data.repository.BaiToan
import com.trilogy.mathlearning.ui.presentation.community.CommunityViewModel
import com.trilogy.mathlearning.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolveHistoryDetailScreen(
    questionId: String,
    onBack: () -> Unit
) {
    val vm: CommunityViewModel = hiltViewModel()
    val state by vm.detailState.collectAsState()

    LaunchedEffect(questionId) {
        vm.loadQuestionDetail(questionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết bài toán AI") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { inner ->
        when (val s = state) {
            is UiState.Loading, UiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Đang tải dữ liệu...")
                }
            }

            is UiState.Failure -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner),
                    contentAlignment = Alignment.Center
                ) {
                    Text(s.error ?: "Lỗi tải chi tiết bài toán")
                }
            }

            is UiState.Success -> {
                val question = s.data
                val aiAnswer = question.answers.firstOrNull { it.isAI }

                val baiToan: BaiToan? = try {
                    aiAnswer?.let { Gson().fromJson(it.content, BaiToan::class.java) }
                } catch (_: Exception) {
                    null
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (!question.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = question.imageUrl,
                            contentDescription = "Ảnh bài toán",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    Text(
                        text = question.topic.ifBlank { "Bài toán AI" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(16.dp))

                    if (baiToan == null) {
                        Text(
                            text = "Không đọc được dữ liệu lời giải.",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = "Loại bài toán",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        MathInlineSentence(raw = baiToan.category)

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Kết quả giải toán",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        MathInlineSentence(raw = baiToan.result)

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Các bước giải",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))

                        baiToan.steps.forEach { step ->
                            MathInlineSentence(raw = "• $step")
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

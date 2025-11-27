@file:Suppress("DEPRECATION")

package com.trilogy.mathlearning.ui.presentation.community

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.material3.shimmer
import com.google.accompanist.placeholder.shimmer
import com.trilogy.mathlearning.domain.model.QuestionResDto
import com.trilogy.mathlearning.ui.presentation.solve_math.TakeMathViewModel
import com.trilogy.mathlearning.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDetailScreen(
    postId: String,
    navController: NavController,
    onBack: () -> Unit = {},
    takeVm: TakeMathViewModel = hiltViewModel(),
    vm: CommunityViewModel = hiltViewModel()
) {
    val state by vm.detailState.collectAsState()
    val uploadState by takeVm.imageUploadState.collectAsState()

    LaunchedEffect(postId) {
        vm.loadQuestionDetail(postId)
    }

    Surface(color = Color(0xFFF6F7FB)) {
        androidx.compose.material3.Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Chi tiết câu hỏi") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                AnswerInputBar(
                    hint = "Viết câu trả lời...",
                    uploadState = uploadState,
                    onPickImage = {
                        val encoded = Uri.encode(postId)
                        navController.navigate("answer_scan/$encoded")
                    },
                    onSend = { text, imageUrl ->
                        vm.createAnswer(postId, text, imageUrl)
                    }
                )
            },
            contentWindowInsets = WindowInsets.systemBars,
            containerColor = Color(0xFFF6F7FB)
        ) { inner ->
            when (val s = state) {
                is UiState.Loading, UiState.Empty -> DetailSkeleton(inner)
                is UiState.Failure -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(inner),
                    contentAlignment = Alignment.Center
                ) {
                    Text(s.error ?: "Lỗi tải câu hỏi")
                }

                is UiState.Success -> DetailContent(
                    inner = inner,
                    post = s.data,
                    onToggleLike = { vm.toggleLike(it) }
                )
            }
        }
    }
}

@Composable
private fun DetailContent(inner: PaddingValues, post: QuestionResDto, onToggleLike: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(inner),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        item { PostHeader(post) }

        if (!post.content.isNullOrBlank()) {
            item {
                Text(
                    text = post.content,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
            }
        }
        if (!post.imageUrl.isNullOrBlank()) {
            item {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE9EEF7))
                )
            }
        }

        item {
            Divider(color = Color(0xFFEDEFF3))
            Spacer(Modifier.height(6.dp))
            Text(
                "Câu trả lời (${post.answers.size})",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }

        items(post.answers, key = { it.id }) { a ->
            val name = a.authorName.ifBlank { a.authorEmail.substringBefore("@") }
            val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            val cs = MaterialTheme.colorScheme
            var liked by remember(a.id) { mutableStateOf(false) }
            var likes by remember(a.id) { mutableIntStateOf(a.likes) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(cs.secondary)
                            .border(3.dp, Color.LightGray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initial, color = cs.onSecondary, fontSize = 16.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(name, fontSize = 13.sp)
                        Text(a.authorEmail, color = Color(0xFF8A8F98), fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(a.content, fontSize = 14.sp, lineHeight = 20.sp)
                if (!a.imageUrl.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    AsyncImage(
                        model = a.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 140.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFE9EEF7))
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconToggleButton(
                        checked = liked,
                        onCheckedChange = {
                            liked = !liked
                            if (liked) {
                                likes += 1
                            } else {
                                likes = (likes - 1).coerceAtLeast(0)
                            }
                            onToggleLike(a.id)
                        }
                    ) {
                        Icon(
                            imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "like",
                            tint = if (liked) Color(0xFFE53935) else Color(0xFF8A8F98)
                        )
                    }
                    Text("$likes", fontSize = 13.sp)
                }
            }
        }

        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun PostHeader(post: QuestionResDto) {
    val name = post.authorName.ifBlank { post.authorEmail.substringBefore("@") }
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val cs = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(cs.secondary)
                .border(3.dp, Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(initial, color = cs.onSecondary, fontSize = 18.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(post.authorEmail, color = Color(0xFF8A8F98), fontSize = 12.sp)
        }
    }
}

@Composable
private fun AnswerInputBar(
    hint: String,
    uploadState: UiState<String>,
    onPickImage: () -> Unit,
    onSend: (content: String, imageUrl: String?) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val isUploading = uploadState is UiState.Loading
    val errorText = (uploadState as? UiState.Failure)?.error
    val attachedImageUrl = (uploadState as? UiState.Success<String>)?.data

    Surface(color = Color.White, tonalElevation = 2.dp, shadowElevation = 2.dp) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            if (isUploading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )
            }

            if (!errorText.isNullOrBlank()) {
                Text(
                    errorText,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(4.dp))
            }

            if (!attachedImageUrl.isNullOrBlank()) {
                Text("Đã đính kèm ảnh", color = Color(0xFF5877F9), fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text(hint) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4
                )
                Spacer(Modifier.width(8.dp))
                TextButton(
                    onClick = onPickImage,
                    enabled = !isUploading
                ) {
                    Text("Ảnh")
                }
                Spacer(Modifier.width(4.dp))
                FilledTonalButton(
                    onClick = {
                        val content = text.trim()
                        if (content.isNotEmpty() || !attachedImageUrl.isNullOrBlank()) {
                            onSend(content, attachedImageUrl?.takeIf { it.isNotBlank() })
                            text = ""
                        }
                    },
                    enabled = (text.isNotBlank() || !attachedImageUrl.isNullOrBlank()) && !isUploading
                ) {
                    Text("Gửi")
                }
            }
        }
    }
}

@Composable
private fun Modifier.shimmer(shape: Shape = RoundedCornerShape(8.dp)) = this.placeholder(
    visible = true,
    shape = shape,
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    highlight = PlaceholderHighlight.shimmer(
        highlightColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
    )
)

@Composable
private fun DetailSkeleton(inner: PaddingValues) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(inner),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .shimmer(CircleShape)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Box(
                        Modifier
                            .fillMaxWidth(0.4f)
                            .height(14.dp)
                            .shimmer()
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        Modifier
                            .fillMaxWidth(0.6f)
                            .height(12.dp)
                            .shimmer()
                    )
                }
            }
        }
        items(2) {
            Box(
                Modifier
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .fillMaxWidth()
                    .height(12.dp)
                    .shimmer()
            )
        }
        item {
            Box(
                Modifier
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shimmer(RoundedCornerShape(12.dp))
            )
        }
        item {
            Divider(color = Color(0xFFEDEFF3))
            Spacer(Modifier.height(6.dp))
            Box(
                Modifier
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .fillMaxWidth(0.3f)
                    .height(16.dp)
                    .shimmer()
            )
        }
        items(3) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .shimmer(CircleShape)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Box(
                        Modifier
                            .fillMaxWidth(0.35f)
                            .height(12.dp)
                            .shimmer()
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .shimmer()
                    )
                }
            }
        }
    }
}

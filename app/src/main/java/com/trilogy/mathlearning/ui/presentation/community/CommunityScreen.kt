@file:Suppress("DEPRECATION")

package com.trilogy.mathlearning.ui.presentation.community

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.shimmer
import com.trilogy.mathlearning.domain.model.QuestionResDto
import com.trilogy.mathlearning.ui.activity.ImageViewerActivity
import com.trilogy.mathlearning.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onOpenPost: (String) -> Unit = {},
    onCreatePost: (() -> Unit)? = null,
    onSharePost: (QuestionResDto) -> Unit = {}
) {
    val vm: CommunityViewModel = hiltViewModel()
    val state by vm.questionsState.collectAsState()

    LaunchedEffect(Unit) { vm.loadQuestions() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Home, contentDescription = null, tint = Color(0xFF1677FF))
                        Spacer(Modifier.width(10.dp))
                        Text("Cộng đồng", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Filled.Search, contentDescription = "Search") }
                    IconButton(onClick = { }) { Icon(Icons.Filled.MoreVert, contentDescription = "More") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onCreatePost?.invoke() },
                containerColor = Color(0xFFFF6D00),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Filled.Edit, contentDescription = "Đặt câu hỏi") }
        },
        containerColor = Color(0xFFF6F7FB)
    ) { inner ->
        when (val s = state) {
            is UiState.Loading, UiState.Empty -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(inner),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) { items(5) { SkeletonPostCard() } }
            }
            is UiState.Failure -> Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                Text(s.error ?: "Lỗi tải câu hỏi")
            }
            is UiState.Success -> {
                val posts = s.data
                if (posts.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                        Text("Chưa có bài viết", color = Color(0xFF9095A0))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(inner),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        items(posts, key = { it.id }) { q ->
                            QuestionCard(
                                question = q,
                                onCardClick = { onOpenPost(q.id) },
                                onCommentClick = { onOpenPost(q.id) },
                                onShareClick = { onSharePost(q) }
                            )
                            Divider(color = Color(0xFFEDEFF3))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionCard(
    question: QuestionResDto,
    onCardClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val name = question.authorName.ifBlank { question.authorEmail.substringBefore("@") }
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val ctx = LocalContext.current

    Surface(color = Color.White) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp)) {
            Column(Modifier.fillMaxWidth().clickable(onClick = onCardClick)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val cs = MaterialTheme.colorScheme
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape)
                            .background(cs.secondary)
                            .border(3.dp, Color.LightGray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text(initial, color = cs.onSecondary, fontSize = 18.sp) }

                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(question.authorEmail, color = Color(0xFF8A8F98), fontSize = 12.sp)
                    }
                    IconButton(onClick = { }) { Icon(Icons.Filled.MoreVert, contentDescription = null) }
                }

                Spacer(Modifier.height(8.dp))

                if (!question.content.isNullOrBlank()) {
                    Text(
                        text = question.content!!,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (!question.imageUrl.isNullOrBlank()) {
                    FitCenterImage(
                        url = question.imageUrl!!,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE9EEF7))
                            .clickable {
                                ctx.startActivity(
                                    Intent(ctx, ImageViewerActivity::class.java)
                                        .putExtra(ImageViewerActivity.EXTRA_URL, question.imageUrl)
                                )
                            }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                ActionIconText(Icons.Outlined.ModeComment, "${question.answers.size}", onCommentClick)
                Spacer(Modifier.weight(1f))
                Text("Chia sẻ", color = Color(0xFF5877F9), fontSize = 13.sp, modifier = Modifier.clickable(onClick = onShareClick))
            }
        }
    }
}

/* ------ helpers (giữ nguyên như bạn đang dùng) ------ */
@Composable private fun ActionIconText(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onClick)) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF8A8F98))
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 13.sp)
    }
}

@Composable private fun FitCenterImage(url: String, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = url, contentDescription = null,
        loading = { Box(modifier.fillMaxWidth().height(180.dp).shimmer(RoundedCornerShape(12.dp))) {} },
        success = { state ->
            val size = state.painter.intrinsicSize
            val aspect = if (size.width > 0 && size.height > 0) size.width / size.height else 16f / 9f
            AsyncImage(model = url, contentDescription = null, contentScale = ContentScale.Fit, modifier = modifier.fillMaxWidth().aspectRatio(aspect))
        },
        error = { Box(modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) { Text("Không tải được ảnh", color = Color(0xFF8A8F98)) } }
    )
}

@Composable private fun Modifier.shimmer(shape: Shape = RoundedCornerShape(8.dp)) = this.placeholder(
    visible = true, shape = shape,
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    highlight = PlaceholderHighlight.shimmer(highlightColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
)

@Composable private fun SkeletonPostCard() {
    Surface(color = Color.White) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(36.dp).clip(CircleShape).shimmer(CircleShape))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Box(Modifier.fillMaxWidth(0.35f).height(14.dp).shimmer())
                    Spacer(Modifier.height(6.dp))
                    Box(Modifier.fillMaxWidth(0.5f).height(12.dp).shimmer())
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.size(20.dp).shimmer(CircleShape))
            }
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth().height(12.dp).shimmer())
            Spacer(Modifier.height(6.dp))
            Box(Modifier.fillMaxWidth(0.9f).height(12.dp).shimmer())
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)).shimmer(RoundedCornerShape(12.dp)))
        }
    }
}

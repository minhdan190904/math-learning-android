@file:Suppress("DEPRECATION")

package com.trilogy.mathlearning.ui.presentation.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.material3.shimmer
import com.google.accompanist.placeholder.shimmer
import com.trilogy.mathlearning.domain.model.QuestionResDto
import com.trilogy.mathlearning.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDetailScreen(
    postId: String,
    onBack: () -> Unit = {}
) {
    val vm: CommunityViewModel = hiltViewModel()
    val state by vm.detailState.collectAsState()

    LaunchedEffect(postId) { vm.loadQuestionDetail(postId) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chi tiết câu hỏi") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            AnswerInputBar(
                hint = "Viết câu trả lời...",
                onSend = { text, imageUrl -> vm.createAnswer(postId, text, imageUrl) }
            )
        },
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
        containerColor = Color(0xFFF6F7FB)
    ) { inner ->
        when (val s = state) {
            is UiState.Loading, UiState.Empty -> DetailSkeleton(inner)
            is UiState.Failure -> Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                Text(s.error ?: "Lỗi tải câu hỏi")
            }
            is UiState.Success -> DetailContent(inner, s.data, onToggleLike = { vm.toggleLike(it, postId) })
        }
    }
}

@Composable
private fun DetailContent(inner: PaddingValues, post: QuestionResDto, onToggleLike: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(inner),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        item { PostHeader(post) }

        if (!post.content.isNullOrBlank()) {
            item {
                Text(
                    text = post.content!!,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    fontSize = 15.sp, lineHeight = 20.sp
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
            Text("Câu trả lời (${post.answers.size})", modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
        }

        items(post.answers, key = { it.id }) { a ->
            val name = a.authorName.ifBlank { a.authorEmail.substringBefore("@") }
            val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            val cs = MaterialTheme.colorScheme
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape)
                            .background(cs.secondary).border(3.dp, Color.LightGray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text(initial, color = cs.onSecondary, fontSize = 16.sp) }
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
                // only LIKE cho answer
                var liked by remember(a.id) { mutableStateOf(false) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconToggleButton(
                        checked = liked,
                        onCheckedChange = {
                            liked = it
                            onToggleLike(a.id)
                        }
                    ) {
                        Icon(
                            imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "like",
                            // chỉ đổi màu icon, KHÔNG đổi nền -> không còn "đỏ cả trong"
                            tint = if (liked) Color(0xFFE53935) else Color(0xFF8A8F98)
                        )
                    }
                    Text("${a.likes}", fontSize = 13.sp)
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
            modifier = Modifier.size(36.dp).clip(CircleShape)
                .background(cs.secondary).border(3.dp, Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(initial, color = cs.onSecondary, fontSize = 18.sp) }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(post.authorEmail, color = Color(0xFF8A8F98), fontSize = 12.sp)
        }
    }
}

/* ---- Bottom input: text + optional image URL (hook sẵn để bạn nối picker/upload) ---- */
@Composable
private fun AnswerInputBar(
    hint: String,
    onSend: (content: String, imageUrl: String?) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) } // bạn nối picker rồi set vào đây

    Surface(color = Color.White, tonalElevation = 2.dp, shadowElevation = 2.dp) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp)) {

            if (!imageUrl.isNullOrBlank()) {
                Text("Đã đính kèm ảnh", color = Color(0xFF5877F9), fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text(hint) },
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4
                )
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = {
                    // TODO: mở picker -> upload -> set imageUrl
                    // tạm thời toggle null <-> demo
                    imageUrl = if (imageUrl == null) "" else null
                }) { Text("Ảnh") }
                Spacer(Modifier.width(4.dp))
                FilledTonalButton(
                    onClick = {
                        val content = text.trim()
                        if (content.isNotEmpty() || !imageUrl.isNullOrBlank()) {
                            onSend(content, imageUrl?.takeIf { it.isNotBlank() })
                            text = ""
                            imageUrl = null
                        }
                    },
                    enabled = text.isNotBlank() || !imageUrl.isNullOrBlank()
                ) { Text("Gửi") }
            }
        }
    }
}

/* ---- Skeleton ---- */
@Composable private fun Modifier.shimmer(shape: Shape = RoundedCornerShape(8.dp)) = this.placeholder(
    visible = true, shape = shape,
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    highlight = PlaceholderHighlight.shimmer(highlightColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
)

@Composable
private fun DetailSkeleton(inner: PaddingValues) {
    LazyColumn(Modifier.fillMaxSize().padding(inner), contentPadding = PaddingValues(bottom = 88.dp)) {
        item {
            Row(
                Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(36.dp).clip(CircleShape).shimmer(CircleShape))
                Spacer(Modifier.width(10.dp))
                Column {
                    Box(Modifier.fillMaxWidth(0.4f).height(14.dp).shimmer())
                    Spacer(Modifier.height(6.dp))
                    Box(Modifier.fillMaxWidth(0.6f).height(12.dp).shimmer())
                }
            }
        }
        items(2) { Box(Modifier.padding(horizontal = 14.dp, vertical = 6.dp).fillMaxWidth().height(12.dp).shimmer()) }
        item { Box(Modifier.padding(horizontal = 14.dp, vertical = 8.dp).fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).shimmer(RoundedCornerShape(12.dp))) }
        item {
            Divider(color = Color(0xFFEDEFF3)); Spacer(Modifier.height(6.dp))
            Box(Modifier.padding(horizontal = 14.dp, vertical = 8.dp).fillMaxWidth(0.3f).height(16.dp).shimmer())
        }
        items(3) {
            Row(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(32.dp).clip(CircleShape).shimmer(CircleShape))
                Spacer(Modifier.width(10.dp))
                Column { Box(Modifier.fillMaxWidth(0.35f).height(12.dp).shimmer()); Spacer(Modifier.height(6.dp)); Box(Modifier.fillMaxWidth().height(10.dp).shimmer()) }
            }
        }
    }
}

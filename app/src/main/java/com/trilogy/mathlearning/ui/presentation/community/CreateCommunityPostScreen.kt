package com.trilogy.mathlearning.ui.presentation.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.trilogy.mathlearning.ui.common.BlockingProgressOverlay
import com.trilogy.mathlearning.ui.presentation.solve_math.CustomImageDisplay
import com.trilogy.mathlearning.ui.presentation.solve_math.TakeMathViewModel
import com.trilogy.mathlearning.utils.UiState
import androidx.compose.foundation.layout.ime // <- chỉ để dùng WindowInsets.ime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommunityPostScreen(
    navController: NavController,
    onPosted: () -> Unit = {},
    takeVm: TakeMathViewModel = hiltViewModel(),     // VM chọn + upload ảnh
    communityVm: CommunityViewModel = hiltViewModel()// VM đăng câu hỏi
) {
    // Ảnh & trạng thái upload từ VM ảnh
    val images by takeVm.images.collectAsState()
    val uploadState by takeVm.imageUploadState.collectAsState()

    // Trạng thái đăng bài từ VM community
    val postState by communityVm.postState.collectAsState()

    var content by rememberSaveable { mutableStateOf("") }

    val canPost = (uploadState is UiState.Success<*>) || content.isNotBlank()
    val isBusy = (uploadState is UiState.Loading) || (postState is UiState.Loading)

    // Khi đăng thành công -> dọn state và callback
    LaunchedEffect(postState) {
        val ok = (postState as? UiState.Success<*>)?.data
        if (ok != null) {
            communityVm.resetPostState()
            onPosted()
        }
    }

    Scaffold(
        // Loại trừ IME inset để bàn phím KHÔNG đẩy layout
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.ime),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Giải bài tập", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { if (!isBusy) navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Đóng")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val url = (uploadState as? UiState.Success<String>)?.data
                            communityVm.createQuestion(content.trim().ifBlank { null }, url)
                        },
                        enabled = canPost && !isBusy
                    ) {
                        Text(
                            "Đăng",
                            color = if (canPost && !isBusy) Color(0xFF1677FF) else Color(0xFFBDBDBD)
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomActionBar(
                count = "${images.size}/1",
                onPickImage = { if (!isBusy) navController.navigate("editor_post") }
            )
        },
        containerColor = Color.White
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner) // KHÔNG dùng imePadding() nữa
        ) {
            // Thanh tiến trình khi upload ảnh
            if (uploadState is UiState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Lỗi upload ảnh
            if (uploadState is UiState.Failure) {
                val msg = (uploadState as UiState.Failure).error ?: "Upload thất bại"
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                TextButton(
                    onClick = { takeVm.uploadCurrentFirstImage() },
                    enabled = !isBusy,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) { Text("Thử lại") }
            }

            // Thumbnail ảnh đầu tiên (nếu có)
            val thumb = images.firstOrNull()
            if (thumb != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = !isBusy) { navController.navigate("editor_post") }
                    ) {
                        CustomImageDisplay(
                            bitmap = thumb,
                            onImageClick = { navController.navigate("editor_post") },
                            backgroundColor = Color.White
                        )
                        IconButton(
                            onClick = { takeVm.removeAt(0) /* nhớ reset upload state trong VM ảnh */ },
                            enabled = !isBusy,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.35f))
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Xoá", tint = Color.White)
                        }
                    }
                }
            }

            // Ô nhập nội dung (đặt cao hơn một chút để tránh vùng bàn phím đè)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                BasicTextField(
                    value = content,
                    onValueChange = { content = it },
                    enabled = !isBusy,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textStyle = LocalTextStyle.current.merge(TextStyle(color = Color(0xFF202124))),
                    decorationBox = { innerTextField ->
                        Box(Modifier.fillMaxWidth()) {
                            if (content.isBlank()) Text("Bạn đang nghĩ gì?", color = Color(0xFFB0B0B0))
                            innerTextField()
                        }
                    }
                )
            }
        }
    }

    // Overlay chặn tương tác khi đang đăng
    BlockingProgressOverlay(visible = postState is UiState.Loading)
}

@Composable
private fun BottomActionBar(
    count: String,
    onPickImage: () -> Unit
) {
    Surface(color = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // KHÔNG dùng imePadding() để không bị đẩy khi mở bàn phím
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#", color = Color(0xFF6F6F6F))
            Spacer(Modifier.width(18.dp))
            Row(
                modifier = Modifier.clickable { onPickImage() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Image, contentDescription = "Chọn ảnh")
                Spacer(Modifier.width(6.dp))
                Text(count, color = Color(0xFF6F6F6F))
            }
        }
    }
}

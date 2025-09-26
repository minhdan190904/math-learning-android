package com.trilogy.mathlearning.ui.presentation.auth.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trilogy.mathlearning.R
import com.trilogy.mathlearning.ui.presentation.auth.AuthViewModel
import com.trilogy.mathlearning.utils.UiState

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
) {

    val context: Context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is UiState.Success) {
            onLoginSuccess()
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D47A1), // Xanh dương rất đậm
            Color(0xFF1976D2), // Xanh dương trung bình
            Color(0xFF42A5F5), // Xanh dương sáng
            Color(0xFF64B5F6), // Xanh dương nhạt hơn
        )

    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .systemBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))

            // Logo tròn “AI”
            AILogo()

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Math Learning",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    fontSize = 40.sp,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.3f),
                        offset = Offset(3f, 3f),
                        blurRadius = 6f
                    )
                )
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Sức mạnh của AI trong việc học môn toán",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(56.dp))

            Text(
                text = "Chào mừng trở lại",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Hãy đăng nhập để tiếp tục hành trình\nchinh phục môn toán của bạn",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(56.dp))

            // Nút Google
            GoogleButton(
                onClick = {
                    authViewModel.loginWithGoogle(context)
                },
                isLoading = authState is UiState.Loading
            )

            Spacer(Modifier.height(26.dp))

            Text(
                text = "Tại sao bạn lại chọn Math Learning?",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(14.dp))

            // 3 feature cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeatureCard(
                    iconRes = R.drawable.ic_robot,
                    title = "Giải toán AI",
                    caption = "Thông minh\nNhanh gọn\nChính xác\nHỗ trợ đa dạng bài toán",
                    height = 220.dp
                )
                FeatureCard(
                    iconRes = R.drawable.ic_target,
                    title = "Lộ trình học",
                    caption = "Lộ trình học\nđược cá nhân hóa",
                )
                FeatureCard(
                    iconRes = R.drawable.ic_group,
                    title = "Cộng đồng",
                    caption = "Cộng đồng học tập\nChia sẻ kiến thức",
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AILogo() {
    // Vòng tròn nền sáng nhẹ để nổi trên gradient
    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        // Vòng tròn trong trắng + chữ AI xanh
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "AI Logo",
                modifier = Modifier.size(72.dp),
                alignment = Alignment.Center
            )
        }
    }
}

@Composable
private fun GoogleButton(onClick: () -> Unit, isLoading: Boolean = false) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContentColor = Color(0xFF42A5F5),
            disabledContainerColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        enabled = !isLoading
    ) {
        if (!isLoading) {
            Icon(
                painter = painterResource(R.drawable.ic_google),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color(0xFF42A5F5),
                strokeWidth = 1.dp
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = if (isLoading) "Đang đăng nhập..." else "Đăng nhập với Google",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal)
        )
    }
}

@Composable
private fun RowScope.FeatureCard(
    iconRes: Int,
    title: String,
    caption: String,
    height: Dp = 180.dp
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(height)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.35f),
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White.copy(alpha = 0.14f),
            contentColor = Color.White,
        ),

        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 14.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

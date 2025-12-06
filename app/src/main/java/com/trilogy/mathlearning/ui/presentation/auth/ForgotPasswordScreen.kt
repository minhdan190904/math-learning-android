package com.trilogy.mathlearning.ui.presentation.auth

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trilogy.mathlearning.R
import com.trilogy.mathlearning.domain.model.ResDto
import com.trilogy.mathlearning.utils.UiState

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    onOpenCodeScreen: (String) -> Unit,      // 🔁 đổi tên + kiểu
    viewModel: AuthViewModel = hiltViewModel()
) {
    val ui by viewModel.forgotPasswordState.collectAsStateWithLifecycle()
    val primaryBlue = Color(0xFF1677FF)
    val cardShape = RoundedCornerShape(22.dp)

    var email by remember { mutableStateOf("") }
    var emailErr by remember { mutableStateOf<String?>(null) }

    val isLoading = ui is UiState.Loading
    val success = ui as? UiState.Success<ResDto>
    val errorMessage = (ui as? UiState.Failure)?.error

    LaunchedEffect(Unit) { viewModel.clearForgotPasswordState() }

    // ✅ Khi gửi mail thành công -> mở màn OTP
    LaunchedEffect(ui) {
        if (success != null) {
            onOpenCodeScreen(email.trim())
            viewModel.clearForgotPasswordState()
        }
    }

    fun validate(): Boolean {
        emailErr = when {
            email.isBlank() -> "Vui lòng nhập email"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email không hợp lệ"
            else -> null
        }
        return emailErr == null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryBlue),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = cardShape,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.login_illustration),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(top = 6.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Quên mật khẩu",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D0D0D),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Nhập email để nhận mã đặt lại mật khẩu.",
                    fontSize = 13.sp,
                    color = Color(0xFF70757A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 6.dp)
                )
                Spacer(Modifier.height(14.dp))
                Label("EMAIL")
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailErr = null
                    },
                    singleLine = true,
                    isError = emailErr != null,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    )
                )
                ErrorHint(emailErr)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (validate()) {
                            viewModel.forgotPassword(email.trim())
                        }
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1677FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Gửi mã", color = Color.White, fontSize = 16.sp)
                    }
                }

                if (!errorMessage.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    )
                }

                Spacer(Modifier.height(18.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nhớ mật khẩu?  ",
                        fontSize = 13.sp,
                        color = Color(0xFF70757A),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Đăng nhập",
                        fontSize = 13.sp,
                        color = Color(0xFF1677FF),
                        modifier = Modifier.clickable { onBackToLogin() }
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

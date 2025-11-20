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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trilogy.mathlearning.R
import com.trilogy.mathlearning.domain.model.LoginResDto
import com.trilogy.mathlearning.utils.UiState

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: (() -> Unit)? = null,
    onForgotPasswordClick: (() -> Unit)? = null,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val ui by viewModel.authState.collectAsStateWithLifecycle()
    val primaryBlue = Color(0xFF1677FF)
    val cardShape = RoundedCornerShape(22.dp)

    LaunchedEffect(ui) {
        (ui as? UiState.Success<*>)?.data?.let {
            if (it is LoginResDto) onLoginSuccess()
        }
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
            ContentBlock(
                isLoading = ui is UiState.Loading,
                onSubmit = { email, pass -> viewModel.login(email, pass) },
                onSignUpClick = onSignUpClick,
                onForgotPasswordClick = onForgotPasswordClick
            )
        }
    }
}

@Composable
private fun ContentBlock(
    isLoading: Boolean,
    onSubmit: (String, String) -> Unit,
    onSignUpClick: (() -> Unit)?,
    onForgotPasswordClick: (() -> Unit)?
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var emailErr by remember { mutableStateOf<String?>(null) }
    var passErr by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        emailErr = when {
            email.isBlank() -> "Vui lòng nhập email"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email không hợp lệ"
            else -> null
        }
        passErr = if (pass.length < 6) "Mật khẩu phải có ít nhất 6 ký tự" else null
        return emailErr == null && passErr == null
    }

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
            text = "Đăng nhập",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D0D0D),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp)
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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        ErrorHint(emailErr)

        Spacer(Modifier.height(12.dp))
        Label("MẬT KHẨU")
        OutlinedTextField(
            value = pass,
            onValueChange = {
                pass = it
                passErr = null
            },
            singleLine = true,
            isError = passErr != null,
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPass = !showPass }) {
                    Icon(
                        if (showPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
        ErrorHint(passErr)

        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
        ) {
            Text(
                "Quên mật khẩu?",
                fontSize = 13.sp,
                color = Color(0xFF1677FF),
                modifier = Modifier.clickable { onForgotPasswordClick?.invoke() }
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { if (validate()) onSubmit(email.trim(), pass) },
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
                Text("Đăng nhập", color = Color.White, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f), color = Color(0xFFE6E6E6))
            Text(
                "Hoặc",
                modifier = Modifier.padding(horizontal = 10.dp),
                fontSize = 13.sp,
                color = Color(0xFF9AA0A6)
            )
            Divider(modifier = Modifier.weight(1f), color = Color(0xFFE6E6E6))
        }

        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Chưa có tài khoản?  ",
                fontSize = 13.sp,
                color = Color(0xFF70757A),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Đăng ký",
                fontSize = 13.sp,
                color = Color(0xFF1677FF),
                modifier = Modifier.clickable { onSignUpClick?.invoke() }
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = Color(0xFF6B7280),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp, start = 2.dp)
    )
}

@Composable
private fun ErrorHint(msg: String?) {
    if (msg != null) {
        Text(
            text = msg,
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 2.dp)
        )
    }
}

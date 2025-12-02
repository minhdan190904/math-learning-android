package com.trilogy.mathlearning.ui.presentation.auth

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trilogy.mathlearning.R
import com.trilogy.mathlearning.domain.model.ResDto
import com.trilogy.mathlearning.utils.UiState

@Composable
fun ResetPasswordScreen(
    onBackToLogin: () -> Unit,
    onResetSuccess: (() -> Unit)? = null,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val ui by viewModel.resetPasswordState.collectAsStateWithLifecycle()
    val primaryBlue = Color(0xFF1677FF)
    val cardShape = RoundedCornerShape(22.dp)

    var code by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var codeErr by remember { mutableStateOf<String?>(null) }
    var newPassErr by remember { mutableStateOf<String?>(null) }
    var confirmPassErr by remember { mutableStateOf<String?>(null) }
    var showNewPass by remember { mutableStateOf(false) }
    var showConfirmPass by remember { mutableStateOf(false) }

    val isLoading = ui is UiState.Loading
    val successMessage = (ui as? UiState.Success<ResDto>)?.data?.message
    val errorMessage = (ui as? UiState.Failure)?.error

    LaunchedEffect(Unit) { viewModel.clearResetPasswordState() }

    LaunchedEffect(ui) {
        if (ui is UiState.Success<*>) {
            onResetSuccess?.invoke()
        }
    }

    fun validate(): Boolean {
        codeErr = if (code.isBlank()) "Vui lòng nhập mã xác nhận" else null
        newPassErr = if (newPass.length < 6) "Mật khẩu phải có ít nhất 6 ký tự" else null
        confirmPassErr = when {
            confirmPass.isBlank() -> "Vui lòng nhập lại mật khẩu"
            confirmPass != newPass -> "Mật khẩu nhập lại không khớp"
            else -> null
        }
        return codeErr == null && newPassErr == null && confirmPassErr == null
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
                    text = "Đặt lại mật khẩu",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D0D0D),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Nhập mã xác nhận và mật khẩu mới của bạn.",
                    fontSize = 13.sp,
                    color = Color(0xFF70757A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 6.dp)
                )
                Spacer(Modifier.height(14.dp))
                Label("MÃ XÁC NHẬN")
                OutlinedTextField(
                    value = code,
                    onValueChange = {
                        code = it
                        codeErr = null
                    },
                    singleLine = true,
                    isError = codeErr != null,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
                ErrorHint(codeErr)
                Spacer(Modifier.height(12.dp))
                Label("MẬT KHẨU MỚI")
                OutlinedTextField(
                    value = newPass,
                    onValueChange = {
                        newPass = it
                        newPassErr = null
                    },
                    singleLine = true,
                    isError = newPassErr != null,
                    visualTransformation = if (showNewPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showNewPass = !showNewPass }) {
                            Icon(
                                if (showNewPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )
                ErrorHint(newPassErr)
                Spacer(Modifier.height(12.dp))
                Label("XÁC NHẬN MẬT KHẨU")
                OutlinedTextField(
                    value = confirmPass,
                    onValueChange = {
                        confirmPass = it
                        confirmPassErr = null
                    },
                    singleLine = true,
                    isError = confirmPassErr != null,
                    visualTransformation = if (showConfirmPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPass = !showConfirmPass }) {
                            Icon(
                                if (showConfirmPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )
                ErrorHint(confirmPassErr)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (validate()) {
                            viewModel.resetPassword(
                                code.trim(),
                                newPass,
                                confirmPass
                            )
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
                        Text("Xác nhận", color = Color.White, fontSize = 16.sp)
                    }
                }
                if (!successMessage.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = successMessage,
                        color = Color(0xFF16A34A),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    )
                }
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
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
                        text = "Quay lại đăng nhập",
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
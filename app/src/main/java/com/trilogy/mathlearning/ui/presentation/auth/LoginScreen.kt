package com.trilogy.mathlearning.ui.presentation.auth

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trilogy.mathlearning.domain.model.LoginResDto
import com.trilogy.mathlearning.utils.UiState

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val ui by viewModel.authState.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    val focus = LocalFocusManager.current

    // Chỉ điều hướng khi nhận Success(LoginResDto)
    LaunchedEffect(ui) {
        val data = (ui as? UiState.Success<*>)?.data
        if (data is LoginResDto) onLoginSuccess()
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHost) }) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            LoginContent(
                isLoading = ui is UiState.Loading,
                onSubmit = { email, pass ->
                    if (validate(email, pass)) {
                        focus.clearFocus()
                        viewModel.login(email.trim(), pass)
                    }
                }
            )
        }
    }
}

@Composable
private fun LoginContent(
    isLoading: Boolean,
    onSubmit: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var emailErr by remember { mutableStateOf<String?>(null) }
    var passErr by remember { mutableStateOf<String?>(null) }

    fun localValidate(): Boolean {
        val (eOk, pOk) = validate(email, password) to (password.length >= 6)
        emailErr = when {
            email.isBlank() -> "Vui lòng nhập email"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email không hợp lệ"
            else -> null
        }
        passErr = if (password.length < 6) "Mật khẩu tối thiểu 6 ký tự" else null
        return emailErr == null && passErr == null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Log In", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; emailErr = null },
            label = { Text("Email") },
            singleLine = true,
            isError = emailErr != null,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        if (emailErr != null) {
            Text(emailErr!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; passErr = null },
            label = { Text("Password") },
            singleLine = true,
            isError = passErr != null,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
        if (passErr != null) {
            Text(passErr!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                if (localValidate()) onSubmit(email, password)
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
            } else {
                Text("Login")
            }
        }
    }
}

private fun validate(email: String, pass: String): Boolean {
    val okEmail = email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val okPass = pass.length >= 6
    return okEmail && okPass
}

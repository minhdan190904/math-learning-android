package com.trilogy.mathlearning.ui.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trilogy.mathlearning.R
import com.trilogy.mathlearning.domain.model.UserDto
import com.trilogy.mathlearning.utils.UiState
import kotlinx.coroutines.delay

@Composable
fun ActivateScreen(
    email: String,
    onActivated: () -> Unit,
    onBack: () -> Unit = {},
    onResendClick: (() -> Unit)? = null,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val ui by viewModel.authState.collectAsStateWithLifecycle()
    val primaryBlue = Color(0xFF1677FF)
    val cardShape = RoundedCornerShape(22.dp)

    LaunchedEffect(ui) {
        (ui as? UiState.Success<*>)?.data?.let { if (it is UserDto) onActivated() }
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
            ActivateContent(
                email = email,
                isLoading = ui is UiState.Loading,
                onSubmit = { code -> viewModel.activate(email, code) },
                onResendClick = onResendClick
            )
        }
    }
}

@Composable
private fun ActivateContent(
    email: String,
    isLoading: Boolean,
    onSubmit: (String) -> Unit,
    onResendClick: (() -> Unit)?
) {
    val primaryBlue = Color(0xFF1677FF)
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var code by remember { mutableStateOf("") } // 6 số
    var seconds by remember { mutableIntStateOf(59) }
    var laidOut by remember { mutableStateOf(false) }

    // Auto-focus + show keyboard khi đã layout
    LaunchedEffect(laidOut) {
        if (laidOut) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    // Countdown 00:59
    LaunchedEffect(Unit) {
        while (seconds > 0) {
            delay(1000)
            seconds -= 1
        }
    }

    fun filtered(input: String) = input.filter { it.isDigit() }.take(6)

    Column(
        modifier = Modifier
            .fillMaxWidth()
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

        Spacer(Modifier.height(8.dp))
        Text(
            text = String.format("00:%02d", seconds),
            color = primaryBlue,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Verification Code",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D0D0D),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Please confirm the security code received on your registered email.",
            color = Color(0xFF6B7280),
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        OtpBoxes(
            code = code,
            length = 6,
            onClick = {
                if (laidOut) {
                    focusRequester.requestFocus()
                    keyboardController?.show()   // <- đảm bảo mở lại bàn phím khi người dùng đã tắt
                }
            }
        )

        // TextField ẩn để nhận số và cập nhật code
        TextField(
            value = code,
            onValueChange = {
                val v = filtered(it)
                code = v
                if (v.length == 6) {
                    // đủ 6 số thì ẩn keyboard (tuỳ luồng của bạn)
                    focusManager.clearFocus()
                }
            },
            modifier = Modifier
                // phải được "place": tránh size(0.dp)
                .fillMaxWidth()
                .height(1.dp)
                .alpha(0f)
                .focusRequester(focusRequester)
                .onGloballyPositioned { laidOut = true },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedTextColor = Color.Transparent,
                focusedTextColor = Color.Transparent,
                disabledTextColor = Color.Transparent,
                cursorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { if (code.length == 6 && !isLoading) onSubmit(code) },
            enabled = code.length == 6 && !isLoading,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(22.dp), color = Color.White)
            } else {
                Text("Confirm", color = Color.White, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(14.dp))
        Text(
            text = "Did not receive the code?",
            color = Color(0xFF70757A),
            fontSize = 13.sp
        )
        Text(
            text = "Send Again",
            color = primaryBlue,
            fontSize = 13.sp,
            modifier = Modifier.clickable {
                if (seconds == 0) {
                    onResendClick?.invoke()
                    seconds = 59
                    // gợi ý: tự động focus + show keyboard lại
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            },
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun OtpBoxes(
    code: String,
    length: Int,
    onClick: () -> Unit
) {
    val boxShape = RoundedCornerShape(8.dp)
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(length) { index ->
            val ch = code.getOrNull(index)?.toString() ?: ""
            Surface(
                shape = boxShape,
                color = Color.White,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                border = BorderStroke(1.dp, Color(0xFFE6E6E6)),
                modifier = Modifier
                    .size(width = 48.dp, height = 56.dp)
                    .clickable { onClick() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = ch,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0D0D0D)
                    )
                }
            }
        }
    }
}

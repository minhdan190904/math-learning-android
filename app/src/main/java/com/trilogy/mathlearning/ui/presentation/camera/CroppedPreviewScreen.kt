package com.trilogy.mathlearning.ui.presentation.camera

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun CroppedPreviewScreen(
    image: Bitmap,
    onClose: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // hiển thị ảnh đã crop (không scale méo)
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = "Cropped",
            contentScale = ContentScale.Fit,   // thấy đủ toàn bộ ảnh crop
            modifier = Modifier.fillMaxSize()
        )

        // thanh info + nút đóng
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0x66000000))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Kết quả: ${image.width} × ${image.height}px",
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = onClose) {
                Text("Xong")
            }
        }
    }
}

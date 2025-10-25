package com.trilogy.mathlearning.ui.presentation.math

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Màu chủ đạo theo ảnh
val Navy = Color(0xFF1E3A8A)      // thanh app bar
val NavyDark = Color(0xFF162C6A)
val AccentYellow = Color(0xFFF8B400) // nút tròn mũi tên / điểm nhấn
val ChipBlue = Color(0xFF2D4EA3)
val ChoiceGray = Color(0xFF444444)
val SuccessGreen = Color(0xFF2DBE60)
val WarnOrange = Color(0xFFFF9900)

val CardStroke = BorderStroke(1.dp, Color(0x1A1E3A8A))

val DialogShape = RoundedCornerShape(20.dp)

@Composable
fun examShapes() = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

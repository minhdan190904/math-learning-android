package com.trilogy.mathlearning.ui.presentation.bottom_navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomDest(val route: String, val label: String, val icon: ImageVector) {
    data object Home : BottomDest("home", "Trang chủ", Icons.Filled.Home)
    data object SolveMath : BottomDest("solve_math", "Giải toán", Icons.Filled.AutoAwesome)
    data object Profile : BottomDest("profile", "Tôi", Icons.Filled.Person)
}

val bottomItems = listOf(BottomDest.Home, BottomDest.SolveMath, BottomDest.Profile)

package com.trilogy.mathlearning.ui.presentation.solve_math

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.trilogy.mathlearning.ui.presentation.navigation.Screen

@Composable
fun SolveMathScreen(
    navController: NavController
) {
    Button(
        onClick = {
            navController.navigate(Screen.TakeMathImage.route)
        }
    ) {
        // Button content
        // You can add text or icons here
        // For example:
        // Text(text = "Solve Math")
    }
}
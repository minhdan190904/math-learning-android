package com.trilogy.mathlearning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.trilogy.mathlearning.ui.presentation.math.Exam
import com.trilogy.mathlearning.ui.presentation.math.ExamListScreen
import com.trilogy.mathlearning.ui.presentation.math.ExamTakingScreen
import com.trilogy.mathlearning.ui.presentation.math.assetText
import com.trilogy.mathlearning.ui.presentation.math.parseExam
import com.trilogy.mathlearning.ui.presentation.math.parseExamIndex
import com.trilogy.mathlearning.ui.presentation.navigation.AppNavigation
import com.trilogy.mathlearning.ui.presentation.navigation.Screen
import com.trilogy.mathlearning.ui.presentation.splash.SplashViewModel
import com.trilogy.mathlearning.ui.theme.MathLearningTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            !splashViewModel.isLoading.value
        }

        setContent {
            MathLearningTheme(darkTheme = false) {
                SetSystemBarsWhite()
                val screen by splashViewModel.startDestination
                AppNavigation(startDestination = screen)

            }
        }
    }
}

@Composable
fun SetSystemBarsWhite() {
    val systemUiController = rememberSystemUiController()

    androidx.compose.runtime.SideEffect {
        systemUiController.setStatusBarColor(
            color = androidx.compose.ui.graphics.Color.White,
            darkIcons = true
        )
        systemUiController.setNavigationBarColor(
            color = androidx.compose.ui.graphics.Color.White,
            darkIcons = true
        )
    }
}

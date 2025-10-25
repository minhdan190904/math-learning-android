package com.trilogy.mathlearning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
        enableEdgeToEdge()

        installSplashScreen().setKeepOnScreenCondition {
            !splashViewModel.isLoading.value
        }

        setContent {
            MathLearningTheme(darkTheme = false) {
                val screen by splashViewModel.startDestination
                AppNavigation(startDestination = Screen.HomeRoot.route)
            }
        }
    }
}
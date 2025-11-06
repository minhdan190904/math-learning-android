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
        enableEdgeToEdge()

        installSplashScreen().setKeepOnScreenCondition {
            !splashViewModel.isLoading.value
        }

        setContent {
            MathLearningTheme(darkTheme = false) {
                val screen by splashViewModel.startDestination
                AppNavigation(startDestination = screen)

            }
        }
    }
}

@Composable
fun TestExamScreens() {
    val ctx = LocalContext.current
    var currentExam by remember { mutableStateOf<Exam?>(null) }

    if (currentExam == null) {
        // đọc danh sách đề
        val list = remember {
            parseExamIndex(assetText(ctx, "exams/exams_index.json"))
        }
        ExamListScreen(
            items = list,
            onOpen = {
                val json = assetText(ctx, "exams/${it.id}.json")
                currentExam = parseExam(json)
            }
        )
    } else {
        // màn làm bài
        ExamTakingScreen(
            exam = currentExam!!,
            onBack = { currentExam = null },
            onSubmit = { answers ->
                println("Kết quả: $answers")
                currentExam = null
            }
        )
    }
}
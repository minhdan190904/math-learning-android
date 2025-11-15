package com.trilogy.mathlearning.ui.presentation.bottom_navigation

import android.graphics.Color
import android.net.Uri
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.trilogy.mathlearning.ui.presentation.community.CommunityScreen
import com.trilogy.mathlearning.ui.presentation.community.QuestionDetailScreen
import com.trilogy.mathlearning.ui.presentation.home.HomeScreen
import com.trilogy.mathlearning.ui.presentation.math.ChapterPickerScreen
import com.trilogy.mathlearning.ui.presentation.math.CheckResultScreen
import com.trilogy.mathlearning.ui.presentation.math.ExamHostScreen
import com.trilogy.mathlearning.ui.presentation.math.PracticeHomeScreen
import com.trilogy.mathlearning.ui.presentation.math.PracticeLoadingScreen
import com.trilogy.mathlearning.ui.presentation.math.PracticeResultScreen
import com.trilogy.mathlearning.ui.presentation.navigation.Screen
import com.trilogy.mathlearning.ui.presentation.profile.ProfileScreen
import com.trilogy.mathlearning.ui.presentation.solve_math.SolveMathScreen

@Composable
fun HomeRoot(
    navControllerApp: NavController,
) {
    val navController = rememberNavController()
    val bottomRoutes = bottomItems.map { it.route }.toSet()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDest = currentBackStack?.destination
    val showBottomBar = currentDest?.route in bottomRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                CustomBottomBar(
                    items = bottomItems,
                    currentRoute = currentDest?.route,
                    onItemClick = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
    ) { inner ->
        NavHost(
            navController = navController,
            startDestination = BottomDest.Home.route,
            modifier = Modifier.padding(bottom = inner.calculateBottomPadding())
        ) {
            composable(
                route = BottomDest.Home.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) {
                HomeScreen()
            }

            composable(
                route = BottomDest.Community.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) {
                CommunityScreen(
                    onOpenPost = { postId ->
                        val encoded = Uri.encode(postId)
                        navController.navigate("question/$encoded")
                    },
                    onCreatePost = { navControllerApp.navigate(Screen.CreatePost.route) }
                )
            }

            composable(
                route = BottomDest.SolveMath.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) {
                SolveMathScreen(navController = navControllerApp)
            }

            composable(
                route = BottomDest.Profile.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) {
                ProfileScreen()
            }

            composable(
                route = BottomDest.Practice.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(BottomDest.Practice.route)
                }
                val vm: com.trilogy.mathlearning.ui.presentation.math.PracticeFlowViewModel =
                    hiltViewModel(parentEntry)

                PracticeHomeScreen(
                    vm = vm,
                    onPickGrade = { grade ->
                        navController.navigate("practice/chapters/$grade")
                    },
                    onOpenHistory = { }
                )
            }

            composable(
                route = "practice/chapters/{grade}",
                arguments = listOf(navArgument("grade") { type = NavType.IntType }),
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(BottomDest.Practice.route)
                }
                val vm: com.trilogy.mathlearning.ui.presentation.math.PracticeFlowViewModel =
                    hiltViewModel(parentEntry)

                val grade = backStackEntry.arguments?.getInt("grade") ?: return@composable
                ChapterPickerScreen(
                    grade = grade,
                    vm = vm,
                    onStartPractice = { },
                    onBack = { navController.popBackStack() },
                    onStartLoading = { g, chapterId ->
                        navController.navigate("practice/loading/$g/$chapterId")
                    }
                )
            }

            composable(
                route = "practice/loading/{grade}/{chapterId}",
                arguments = listOf(
                    navArgument("grade") { type = NavType.IntType },
                    navArgument("chapterId") { type = NavType.IntType }
                ),
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(BottomDest.Practice.route)
                }
                val vm: com.trilogy.mathlearning.ui.presentation.math.PracticeFlowViewModel =
                    hiltViewModel(parentEntry)

                val g = backStackEntry.arguments?.getInt("grade") ?: return@composable
                val c = backStackEntry.arguments?.getInt("chapterId") ?: return@composable
                PracticeLoadingScreen(
                    grade = g,
                    chapterId = c,
                    vm = vm,
                    onReady = { pid ->
                        navController.navigate("practice/play/$pid") {
                            popUpTo("practice/loading/$g/$c") { inclusive = true }
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                route = "practice/play/{practiceId}",
                arguments = listOf(navArgument("practiceId") { type = NavType.StringType }),
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(BottomDest.Practice.route)
                }
                val vm: com.trilogy.mathlearning.ui.presentation.math.PracticeFlowViewModel =
                    hiltViewModel(parentEntry)

                val pid = backStackEntry.arguments?.getString("practiceId") ?: return@composable
                ExamHostScreen(
                    practiceId = pid,
                    vm = vm,
                    onBack = { navController.popBackStack() },
                    onSubmitted = { donePracticeId ->
                        navController.navigate("practice/result/$donePracticeId") {
                            popUpTo("practice/play/$pid") { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = "practice/result/{practiceId}",
                arguments = listOf(navArgument("practiceId") { type = NavType.StringType }),
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(BottomDest.Practice.route)
                }
                val vm: com.trilogy.mathlearning.ui.presentation.math.PracticeFlowViewModel =
                    hiltViewModel(parentEntry)

                val pid = backStackEntry.arguments?.getString("practiceId") ?: return@composable
                PracticeResultScreen(
                    vm = vm,
                    onViewDetail = { id ->
                        navController.navigate("practice/check/$id") {
                            popUpTo("practice/result/$id") { inclusive = true }
                        }
                    },
                    onBackToChapters = {
                        navController.popBackStack(route = BottomDest.Practice.route, inclusive = false)
                    }
                )
            }

            composable(
                route = "practice/check/{practiceId}",
                arguments = listOf(navArgument("practiceId") { type = NavType.StringType }),
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(BottomDest.Practice.route)
                }
                val vm: com.trilogy.mathlearning.ui.presentation.math.PracticeFlowViewModel =
                    hiltViewModel(parentEntry)

                val pid = backStackEntry.arguments?.getString("practiceId") ?: return@composable
                CheckResultScreen(
                    practiceId = pid,
                    vm = vm,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "question/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType }),
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("postId") ?: return@composable
                QuestionDetailScreen(
                    postId = id,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

private fun NavDestination?.isOnDestination(route: String): Boolean =
    this?.hierarchy?.any { it.route == route } == true

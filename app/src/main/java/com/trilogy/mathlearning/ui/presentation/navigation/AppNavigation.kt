package com.trilogy.mathlearning.ui.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.trilogy.mathlearning.ui.presentation.auth.ActivateScreen
import com.trilogy.mathlearning.ui.presentation.auth.ForgotCodeScreen
import com.trilogy.mathlearning.ui.presentation.auth.ForgotPasswordScreen
import com.trilogy.mathlearning.ui.presentation.auth.LoginScreen
import com.trilogy.mathlearning.ui.presentation.auth.RegisterScreen
import com.trilogy.mathlearning.ui.presentation.auth.ResetPasswordScreen
import com.trilogy.mathlearning.ui.presentation.bottom_navigation.HomeRoot
import com.trilogy.mathlearning.ui.presentation.camera.CropEditorScreen
import com.trilogy.mathlearning.ui.presentation.camera.CroppedPreviewScreen
import com.trilogy.mathlearning.ui.presentation.camera.EditorViewModel
import com.trilogy.mathlearning.ui.presentation.camera.ScanCropScreen
import com.trilogy.mathlearning.ui.presentation.community.CreateCommunityPostScreen
import com.trilogy.mathlearning.ui.presentation.solve_math.TakeMathImages
import com.trilogy.mathlearning.ui.presentation.solve_math.TakeMathViewModel
import com.trilogy.mathlearning.ui.presentation.splash.SplashScreen
import com.trilogy.mathlearning.ui.presentation.splash.WelcomeScreen

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = Screen.Welcome.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
        ) {
            WelcomeScreen(navController = navController)
        }

        composable(
            route = Screen.ForgotCode.route + "/{email}",
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ForgotCodeScreen(
                email = email,
                onCodeConfirmed = { code ->
                    navController.navigate(Screen.ResetPassword.route + "/$code")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Splash.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
        ) {
            SplashScreen()
        }

        composable(
            route = Screen.CreatePost.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
        ) {
            CreateCommunityPostScreen(
                navController = navController,
                onPosted = { navController.popBackStack() }
            )
        }

        navigation(
            startDestination = Screen.ScanCrop.route,
            route = "editor_post"
        ) {
            composable(
                route = Screen.ScanCrop.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("editor_post")
                }
                val vm: EditorViewModel = hiltViewModel(parentEntry)

                ScanCropScreen { bmp, rect, origin ->
                    vm.setInput(bmp, rect, origin)
                    navController.navigate(Screen.CropEdit.route)
                }
            }

            composable(
                route = Screen.CropEdit.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("editor_post")
                }
                val vm: EditorViewModel = hiltViewModel(parentEntry)

                val bmp = vm.bmp ?: run { navController.popBackStack(); return@composable }
                val rect = vm.rect ?: run { navController.popBackStack(); return@composable }
                val origin = vm.origin ?: run { navController.popBackStack(); return@composable }

                CropEditorScreen(
                    bitmap = bmp,
                    initialRect = rect,
                    origin = origin,
                    onDone = { cropped ->
                        vm.setCropped(cropped)
                        navController.navigate(Screen.CroppedPreview.route)
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ResetPassword.route + "/{code}",
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val code = backStackEntry.arguments?.getString("code") ?: ""
                ResetPasswordScreen(
                    code = code,
                    onBackToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.ResetPassword.route + "/{code}") { inclusive = true }
                        }
                    },
                    onResetSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.ResetPassword.route + "/{code}") { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.ForgotPassword.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) {
                ForgotPasswordScreen(
                    onBackToLogin = { navController.popBackStack() },
                    onOpenCodeScreen = { email ->
                        navController.navigate(Screen.ForgotCode.route + "/$email")
                    }
                )
            }

            composable(
                route = Screen.ForgotPassword.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) {
                ForgotPasswordScreen(
                    onBackToLogin = { navController.popBackStack() },
                    onOpenCodeScreen = { email ->
                        navController.navigate(Screen.ForgotCode.route + "/$email")
                    }
                )
            }


            composable(
                route = Screen.CroppedPreview.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val editorEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("editor_post")
                }
                val vmEditor: EditorViewModel = hiltViewModel(editorEntry)

                val hostEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.CreatePost.route)
                }
                val hostVm: TakeMathViewModel = hiltViewModel(hostEntry)

                val cropped = vmEditor.cropped ?: run { navController.popBackStack(); return@composable }

                CroppedPreviewScreen(
                    image = cropped,
                    onClose = {
                        hostVm.replaceWithAndUpload(cropped)
                        navController.popBackStack("editor_post", inclusive = true)
                    }
                )
            }
        }

        composable(
            route = Screen.Register.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
        ) {
            RegisterScreen(
                onGoActivate = {
                    navController.navigate(Screen.Activate.route + "/$it")
                },
                onSignInClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Activate.route + "/{email}",
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
        ) {
            val email = it.arguments?.getString("email")
            ActivateScreen(
                email = email ?: "",
                onBack = {
                    navController.popBackStack()
                },
                onActivated = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Activate.route + "/{email}") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Login.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.HomeRoot.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.Register.route)
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }


        composable(
            route = Screen.TakeMathImage.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
        ) {
            TakeMathImages(navController = navController)
        }

        composable(
            route = Screen.HomeRoot.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
        ) {
            HomeRoot(
                navControllerApp = navController,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.HomeRoot.route) { inclusive = true }
                    }
                }
            )
        }

        navigation(
            startDestination = Screen.ScanCrop.route,
            route = "editor"
        ) {
            composable(
                route = Screen.ScanCrop.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("editor")
                }
                val vm: EditorViewModel = hiltViewModel(parentEntry)

                ScanCropScreen { bmp, rect, origin ->
                    vm.setInput(bmp, rect, origin)
                    navController.navigate(Screen.CropEdit.route)
                }
            }

            composable(
                route = Screen.CropEdit.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("editor")
                }
                val vm: EditorViewModel = hiltViewModel(parentEntry)

                val bmp = vm.bmp ?: run { navController.popBackStack(); return@composable }
                val rect = vm.rect ?: run { navController.popBackStack(); return@composable }
                val origin = vm.origin ?: run { navController.popBackStack(); return@composable }

                CropEditorScreen(
                    bitmap = bmp,
                    initialRect = rect,
                    origin = origin,
                    onDone = { cropped ->
                        vm.setCropped(cropped)
                        navController.navigate(Screen.CroppedPreview.route)
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.CroppedPreview.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
            ) { backStackEntry ->
                val editorEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("editor")
                }
                val vmEditor: EditorViewModel = hiltViewModel(editorEntry)

                val hostEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.TakeMathImage.route)
                }
                val hostVm: TakeMathViewModel = hiltViewModel(hostEntry)

                val cropped = vmEditor.cropped ?: run { navController.popBackStack(); return@composable }

                CroppedPreviewScreen(
                    image = cropped,
                    onClose = {
                        hostVm.addImage(cropped)
                        navController.popBackStack("editor", inclusive = true)
                    }
                )
            }
        }
    }
}

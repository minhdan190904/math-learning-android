package com.trilogy.mathlearning.ui.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.trilogy.mathlearning.ui.presentation.auth.LoginScreen
import com.trilogy.mathlearning.ui.presentation.auth.RegisterScreen
import com.trilogy.mathlearning.ui.presentation.bottom_navigation.HomeRoot
import com.trilogy.mathlearning.ui.presentation.camera.CropEditorScreen
import com.trilogy.mathlearning.ui.presentation.camera.CroppedPreviewScreen
import com.trilogy.mathlearning.ui.presentation.camera.EditorViewModel
import com.trilogy.mathlearning.ui.presentation.camera.ScanCropScreen
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

        composable(Screen.Welcome.route) {
            // Welcome Screen
            WelcomeScreen(navController = navController)
        }

        composable(Screen.Splash.route){
            SplashScreen()
        }


        //Register
        composable(Screen.Register.route) {
            RegisterScreen()
        }

        // Login
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.HomeRoot.route) }
            )
        }

        //Take Math Image
        composable(Screen.TakeMathImage.route) {
            TakeMathImages(navController = navController)
        }

        composable(Screen.HomeRoot.route){
            HomeRoot(navControllerApp = navController)
        }

        // Nested graph Editor (Scan → Crop → Preview)
        navigation(
            startDestination = Screen.ScanCrop.route,
            route = "editor"
        ) {
            // Scan
            composable(Screen.ScanCrop.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("editor")
                }
                val vm: EditorViewModel = hiltViewModel(parentEntry)

                ScanCropScreen { bmp, rect, origin ->
                    vm.setInput(bmp, rect, origin)
                    navController.navigate(Screen.CropEdit.route)
                }
            }

            // Crop
            composable(Screen.CropEdit.route) { backStackEntry ->
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

            // Preview
            composable(Screen.CroppedPreview.route) { backStackEntry ->
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
                        // (tuỳ chọn) downscale trước khi add để tiết kiệm RAM
                        hostVm.addImage(cropped /*.downscale()*/)
                        navController.popBackStack("editor", inclusive = true)
                    }
                )
            }

        }
    }
}

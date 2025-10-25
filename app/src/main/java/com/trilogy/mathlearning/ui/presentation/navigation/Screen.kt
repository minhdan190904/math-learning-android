package com.trilogy.mathlearning.ui.presentation.navigation


sealed class Screen(val route: String) {
    data object ScanCrop : Screen("scan_crop_screen")
    data object CropEdit : Screen("crop_edit_screen")
    data object CroppedPreview : Screen("cropped_preview_screen")
    data object Login : Screen("login_screen")
    data object Register : Screen("register_screen")
    data object Activate : Screen("activate_screen")
    data object Welcome : Screen(route = "welcome_screen")
    data object Splash : Screen(route = "splash_screen")
    data object HomeRoot : Screen(route = "home_root_screen")
    data object TakeMathImage: Screen("take_math_image_screen")
    data object Practice: Screen("practice_screen")
    data object Community: Screen("community_screen")
    data object Profile: Screen("profile_screen")
}

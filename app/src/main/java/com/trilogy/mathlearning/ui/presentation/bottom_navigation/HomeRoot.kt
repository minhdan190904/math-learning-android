package com.trilogy.mathlearning.ui.presentation.bottom_navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.trilogy.mathlearning.ui.presentation.community.CommunityScreen
import com.trilogy.mathlearning.ui.presentation.home.HomeScreen
import com.trilogy.mathlearning.ui.presentation.practice.PracticeScreen
import com.trilogy.mathlearning.ui.presentation.profile.ProfileScreen
import com.trilogy.mathlearning.ui.presentation.solve_math.SolveMathScreen

@Composable
fun HomeRoot(
    navControllerApp: NavController,
) {
    val navController = rememberNavController()
    val bottomRoutes = remember { bottomItems.map { it.route }.toSet() }
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
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { inner ->
        NavHost(
            navController = navController,
            startDestination = BottomDest.Home.route,
            modifier = Modifier.padding(inner)
        ) {
            composable(BottomDest.Home.route) { HomeScreen() }
            composable(BottomDest.Community.route) { CommunityScreen() }
            composable(BottomDest.Practice.route) { PracticeScreen() }
            composable(BottomDest.SolveMath.route) { SolveMathScreen(navController = navControllerApp) }
            composable(BottomDest.Profile.route) { ProfileScreen() }
        }
    }
}

private fun NavDestination?.isOnDestination(route: String): Boolean =
    this?.hierarchy?.any { it.route == route } == true

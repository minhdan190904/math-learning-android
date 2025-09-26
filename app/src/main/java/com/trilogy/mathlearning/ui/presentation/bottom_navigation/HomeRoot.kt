package com.trilogy.mathlearning.ui.presentation.bottom_navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.trilogy.mathlearning.ui.presentation.home.HomeScreen
import com.trilogy.mathlearning.ui.presentation.profile.ProfileScreen
import com.trilogy.mathlearning.ui.presentation.solve_math.SolveMathScreen
import com.trilogy.mathlearning.ui.presentation.solve_math.TakeMathImages

@Composable
fun HomeRoot(
    navControllerApp: NavController,
) {
    val navController = rememberNavController()

    val bottomRoutes = remember { bottomItems.map { it.route }.toSet() }

    // quan sát destination hiện tại
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDest = currentBackStack?.destination
    val showBottomBar = currentDest?.route in bottomRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color.White.copy(0.5f)) {
                    bottomItems.forEach { item ->
                        val selected =
                            currentDest.isOnDestination(item.route)

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF6A50FF),
                                selectedTextColor = Color(0xFF6A50FF),
                                unselectedIconColor = Color(0xFFBDBDBD),
                                unselectedTextColor = Color(0xFFBDBDBD),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { inner ->
        NavHost(
            navController = navController,
            startDestination = BottomDest.Home.route,
            modifier = Modifier.padding(inner)
        ) {
            composable(BottomDest.Home.route) { HomeScreen() }
            composable(BottomDest.SolveMath.route) { SolveMathScreen(navController = navControllerApp) }
            composable(BottomDest.Profile.route) { ProfileScreen() }

        }
    }
}

private fun NavDestination?.isOnDestination(route: String): Boolean =
    this?.hierarchy?.any { it.route == route } == true

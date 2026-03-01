package com.masterofpuppets.thepips.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.masterofpuppets.thepips.feature.dashboard.DashboardScreen
import com.masterofpuppets.thepips.feature.info.InfoScreen
import com.masterofpuppets.thepips.feature.library.LibraryScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToInfo = {
                    navController.navigate(Screen.Info.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(route = Screen.Library.route) {
            LibraryScreen()
        }
        composable(route = Screen.Info.route) {
            InfoScreen()
        }
    }
}
package com.markscene.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.markscene.app.ui.screen.HomeScreen
import com.markscene.app.ui.screen.PlaceholderScreen

private const val HOME_ROUTE = "home"
private const val CREATE_RECORD_ROUTE = "create_record"
private const val SEARCH_ROUTE = "search"
private const val SETTINGS_ROUTE = "settings"

@Composable
fun MarkSceneApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE
    ) {
        composable(HOME_ROUTE) {
            HomeScreen(
                onCapturePhoto = { navController.navigate(CREATE_RECORD_ROUTE) },
                onImportPhoto = { navController.navigate(CREATE_RECORD_ROUTE) },
                onOpenSettings = { navController.navigate(SETTINGS_ROUTE) },
                onOpenSearch = { navController.navigate(SEARCH_ROUTE) }
            )
        }
        composable(CREATE_RECORD_ROUTE) {
            PlaceholderScreen(title = "Create Record")
        }
        composable(SEARCH_ROUTE) {
            PlaceholderScreen(title = "Record List / Search")
        }
        composable(SETTINGS_ROUTE) {
            PlaceholderScreen(title = "Settings")
        }
    }
}

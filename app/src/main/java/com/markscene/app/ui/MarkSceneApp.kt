package com.markscene.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.markscene.app.ui.screen.CreateRecordScreen
import com.markscene.app.ui.screen.HomeScreen
import com.markscene.app.ui.screen.PlaceholderScreen

private const val HOME_ROUTE = "home"
private const val CREATE_RECORD_ROUTE = "create_record"
private const val CREATE_RECORD_SOURCE_ARG = "source"
private const val SEARCH_ROUTE = "search"
private const val SETTINGS_ROUTE = "settings"

private const val SOURCE_CAPTURE = "capture"
private const val SOURCE_IMPORT = "import"

@Composable
fun MarkSceneApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE
    ) {
        composable(HOME_ROUTE) {
            HomeScreen(
                onCapturePhoto = {
                    navController.navigate("$CREATE_RECORD_ROUTE/$SOURCE_CAPTURE")
                },
                onImportPhoto = {
                    navController.navigate("$CREATE_RECORD_ROUTE/$SOURCE_IMPORT")
                },
                onOpenSettings = { navController.navigate(SETTINGS_ROUTE) },
                onOpenSearch = { navController.navigate(SEARCH_ROUTE) }
            )
        }
        composable(
            route = "$CREATE_RECORD_ROUTE/{$CREATE_RECORD_SOURCE_ARG}",
            arguments = listOf(navArgument(CREATE_RECORD_SOURCE_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val source = backStackEntry.arguments?.getString(CREATE_RECORD_SOURCE_ARG).orEmpty()
            CreateRecordScreen(
                source = source,
                onBack = { navController.popBackStack() }
            )
        }
        composable(SEARCH_ROUTE) {
            PlaceholderScreen(title = "Record List / Search")
        }
        composable(SETTINGS_ROUTE) {
            PlaceholderScreen(title = "Settings")
        }
    }
}

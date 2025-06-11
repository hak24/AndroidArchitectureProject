package com.example.androidarchitectureproject.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.androidarchitectureproject.presentation.home.HomeScreen
import com.example.androidarchitectureproject.presentation.detail.ImageDetailScreen
import com.example.androidarchitectureproject.presentation.favorites.FavoritesScreen
import com.example.androidarchitectureproject.presentation.settings.SettingsScreen

@Composable
fun Navigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onImageClick = { imageId ->
                    navController.navigate(Screen.ImageDetail.createRoute(imageId))
                }
            )
        }
        
        composable(
            route = Screen.ImageDetail.route,
            arguments = Screen.ImageDetail.arguments
        ) {
            ImageDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onImageClick = { imageId ->
                    navController.navigate(Screen.ImageDetail.createRoute(imageId))
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Favorites : Screen("favorites")
    data object Settings : Screen("settings")
    data object ImageDetail : Screen("image/{imageId}") {
        fun createRoute(imageId: String) = "image/$imageId"
        
        val arguments = listOf(
            navArgument("imageId") { type = NavType.StringType }
        )
    }
} 
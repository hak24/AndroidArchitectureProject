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
import com.example.androidarchitectureproject.presentation.detail.InteractiveImageScreen
import com.example.androidarchitectureproject.presentation.favorites.FavoritesScreen
import com.example.androidarchitectureproject.presentation.settings.SettingsScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
                },
                onImageClick = { imageUrl, description ->
                    navController.navigate(
                        Screen.InteractiveImage.createRoute(
                            imageUrl = imageUrl,
                            description = description ?: ""
                        )
                    )
                }
            )
        }
        
        composable(
            route = Screen.InteractiveImage.route,
            arguments = Screen.InteractiveImage.arguments
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
            val encodedDescription = backStackEntry.arguments?.getString("description") ?: ""
            
            val imageUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
            val description = URLDecoder.decode(encodedDescription, StandardCharsets.UTF_8.toString())
            
            InteractiveImageScreen(
                imageUrl = imageUrl,
                description = description,
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
    
    data object InteractiveImage : Screen("interactive_image?imageUrl={imageUrl}&description={description}") {
        fun createRoute(imageUrl: String, description: String): String {
            val encodedUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString())
            val encodedDescription = URLEncoder.encode(description, StandardCharsets.UTF_8.toString())
            return "interactive_image?imageUrl=$encodedUrl&description=$encodedDescription"
        }
        
        val arguments = listOf(
            navArgument("imageUrl") { 
                type = NavType.StringType
                nullable = false
            },
            navArgument("description") { 
                type = NavType.StringType
                nullable = true
                defaultValue = ""
            }
        )
    }
} 
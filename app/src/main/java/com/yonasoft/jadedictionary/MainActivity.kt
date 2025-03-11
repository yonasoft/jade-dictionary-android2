package com.yonasoft.jadedictionary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.yonasoft.jadedictionary.core.navigation.MainRoutes
import com.yonasoft.jadedictionary.core.navigation.WordRoutes
import com.yonasoft.jadedictionary.features.home.presentation.screens.Home
import com.yonasoft.jadedictionary.features.word_search.presentation.screens.WordDetail
import com.yonasoft.jadedictionary.features.word_search.presentation.screens.WordSearch
import com.yonasoft.jadedictionary.ui.theme.JadeDictionaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        setContent {
            JadeDictionaryTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = MainRoutes.Home.name,
                ) {
                    composable(route = MainRoutes.Home.name) {
                        Home(navController = navController)
                    }

                    navigation(
                        startDestination = WordRoutes.WordSearch.route,
                        route = MainRoutes.Words.name
                    ) {
                        composable(route = WordRoutes.WordSearch.route) {
                            WordSearch(
                                navController = navController,
                            )
                        }
                        composable(
                            route = WordRoutes.WordDetail.route,
                            arguments = listOf(
                                navArgument("wordId") { type = NavType.LongType }
                            )
                        ) {
                            WordDetail(
                                navController = navController,
                            )
                        }
                    }
                }
            }
        }
    }
}


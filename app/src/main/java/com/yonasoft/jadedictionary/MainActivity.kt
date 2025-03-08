package com.yonasoft.jadedictionary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yonasoft.jadedictionary.core.navigation.MainNavigation
import com.yonasoft.jadedictionary.features.home.presentation.screens.Home
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
                    startDestination = MainNavigation.Home.name,
                ){
                    composable(route = MainNavigation.Home.name) {
                        Home(navController = navController)
                    }
                    composable(route = MainNavigation.WordSearch.name) {
                        WordSearch(navController = navController)
                    }
                }
            }
        }
    }
}


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
import com.yonasoft.jadedictionary.core.navigation.WordListRoutes
import com.yonasoft.jadedictionary.core.navigation.WordRoutes
import com.yonasoft.jadedictionary.features.home.presentation.screens.Home
import com.yonasoft.jadedictionary.features.word_lists.presentation.screens.WordListDetailScreen
import com.yonasoft.jadedictionary.features.word_lists.presentation.screens.WordLists
import com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels.WordListDetailViewModel
import com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels.WordListsViewModel
import com.yonasoft.jadedictionary.features.word_search.presentation.screens.WordDetail
import com.yonasoft.jadedictionary.features.word_search.presentation.screens.WordSearch
import com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels.WordDetailViewModel
import com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels.WordSearchViewModel
import com.yonasoft.jadedictionary.ui.theme.JadeDictionaryTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

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
                            val wordSearchViewModel = koinViewModel<WordSearchViewModel>()
                            WordSearch(
                                navController = navController,
                                wordSearchViewModel = wordSearchViewModel
                            )
                        }
                        composable(
                            route = WordRoutes.WordDetail.route,
                            arguments = listOf(
                                navArgument("wordId") { type = NavType.LongType }
                            )
                        ) {
                            val wordDetailViewModel = koinViewModel<WordDetailViewModel> {
                                parametersOf(it.savedStateHandle)
                            }
                            WordDetail(
                                navController = navController,
                                wordDetailViewModel = wordDetailViewModel
                            )
                        }
                    }

                    navigation(
                        startDestination = WordListRoutes.WordLists.route,
                        route = MainRoutes.WordLists.name
                    ) {
                        // Word Lists main screen
                        composable(route = WordListRoutes.WordLists.route) {
                            val wordListsViewModel = koinViewModel<WordListsViewModel>()
                            WordLists(
                                navController = navController,
                                wordListsViewModel = wordListsViewModel,
                            )
                        }

                        // Word List Detail screen
                        composable(
                            route = WordListRoutes.WordListDetail.route,
                            arguments = listOf(
                                navArgument("wordListId") { type = NavType.LongType }
                            )
                        ) {
                            val wordListDetailViewModel = koinViewModel<WordListDetailViewModel> {
                                parametersOf(it.savedStateHandle)
                            }
                            WordListDetailScreen(
                                navController = navController,
                                viewModel = wordListDetailViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
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
import com.yonasoft.jadedictionary.core.navigation.PracticeRoutes
import com.yonasoft.jadedictionary.core.navigation.WordListRoutes
import com.yonasoft.jadedictionary.core.navigation.WordRoutes
import com.yonasoft.jadedictionary.features.home.presentation.screens.Home
import com.yonasoft.jadedictionary.features.practice.presentation.screens.cc_setup.CCPracticeSetup
import com.yonasoft.jadedictionary.features.practice.presentation.screens.main.PracticeSelection
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.CCPracticeSetupViewModel
import com.yonasoft.jadedictionary.features.word.presentation.screens.CCWordDetail
import com.yonasoft.jadedictionary.features.word.presentation.screens.HSKWordDetail
import com.yonasoft.jadedictionary.features.word.presentation.viewmodels.CCWordDetailViewModel
import com.yonasoft.jadedictionary.features.word.presentation.viewmodels.HSKWordDetailViewModel
import com.yonasoft.jadedictionary.features.word_lists.presentation.screens.WordListDetailScreen
import com.yonasoft.jadedictionary.features.word_lists.presentation.screens.WordLists
import com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels.WordListDetailViewModel
import com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels.WordListsViewModel
import com.yonasoft.jadedictionary.features.word_search.presentation.screens.WordSearch
import com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels.WordSearchViewModel
import com.yonasoft.jadedictionary.ui.theme.JadeDictionaryTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure window to handle edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Explicitly set the status bar appearance to ensure text is visible
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false // Force white status bar icons
        }

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
                            route = WordRoutes.CCWordDetail.route,
                            arguments = listOf(
                                navArgument("wordId") { type = NavType.LongType }
                            )
                        ) {
                            val ccWordDetailViewModel = koinViewModel<CCWordDetailViewModel> {
                                parametersOf(it.savedStateHandle)
                            }
                            CCWordDetail(
                                navController = navController,
                                ccWordDetailViewModel = ccWordDetailViewModel
                            )
                        }

                        composable(
                            route = WordRoutes.HSKWordDetail.route,
                            arguments = listOf(
                                navArgument("wordId") { type = NavType.LongType }
                            )
                        ) {
                            val hskWordDetailViewModel = koinViewModel<HSKWordDetailViewModel> {
                                parametersOf(it.savedStateHandle)
                            }
                            HSKWordDetail(
                                navController = navController,
                                hskWordDetailViewModel = hskWordDetailViewModel
                            )
                        }
                    }

                    navigation(
                        startDestination = WordListRoutes.WordLists.route,
                        route = MainRoutes.WordLists.name
                    ) {
                        composable(route = WordListRoutes.WordLists.route) {
                            val wordListsViewModel = koinViewModel<WordListsViewModel>()
                            WordLists(
                                navController = navController,
                                wordListsViewModel = wordListsViewModel,
                            )
                        }

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
                    navigation(
                        startDestination = PracticeRoutes.PracticeSelection.route,
                        route = MainRoutes.Practice.name
                    ) {
                        composable(route = PracticeRoutes.PracticeSelection.route) {
                            PracticeSelection(
                                navController = navController
                            )
                        }

                        composable(route = PracticeRoutes.CCPracticeSetup.route,
                            arguments = listOf(
                                navArgument("practiceType") { type = NavType.StringType }
                            )
                        ) {
                            val ccPracticeSetupViewModel = koinViewModel<CCPracticeSetupViewModel> {
                                parametersOf(it.savedStateHandle)
                            }
                            CCPracticeSetup(
                                navController = navController,
                                viewModel = ccPracticeSetupViewModel,
                            )
                        }
                    }
                }
            }
        }
    }
}
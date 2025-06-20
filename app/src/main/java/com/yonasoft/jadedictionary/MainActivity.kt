package com.yonasoft.jadedictionary

import ListeningPractice
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.yonasoft.jadedictionary.core.navigation.MainRoutes
import com.yonasoft.jadedictionary.core.navigation.PracticeRoutes
import com.yonasoft.jadedictionary.core.navigation.SettingsRoutes
import com.yonasoft.jadedictionary.core.navigation.WordListRoutes
import com.yonasoft.jadedictionary.core.navigation.WordRoutes
import com.yonasoft.jadedictionary.core.stores.settings.ThemePreferences
import com.yonasoft.jadedictionary.features.home.presentation.screens.Home
import com.yonasoft.jadedictionary.features.practice.presentation.screens.main.PracticeSelection
import com.yonasoft.jadedictionary.features.practice.presentation.screens.practice_modes.FlashCardPractice
import com.yonasoft.jadedictionary.features.practice.presentation.screens.practice_modes.MultipleChoicePractice
import com.yonasoft.jadedictionary.features.practice.presentation.screens.setup.CCPracticeSetup
import com.yonasoft.jadedictionary.features.practice.presentation.screens.setup.HSKPracticeSetup
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.CCPracticeSetupViewModel
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.FlashCardPracticeViewModel
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.HSKPracticeSetupViewModel
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.ListeningPracticeViewModel
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.MultipleChoicePracticeViewModel
import com.yonasoft.jadedictionary.features.settings.presentation.screens.main.SettingsScreen
import com.yonasoft.jadedictionary.features.settings.presentation.viewmodels.SettingsViewModel
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

            val context = LocalContext.current
            val themePreferences = remember { ThemePreferences(context) }
            val isDarkTheme by themePreferences.isDarkTheme.collectAsState(initial = false)

            JadeDictionaryTheme(darkTheme = isDarkTheme) {
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

                        composable(route = PracticeRoutes.HSKPracticeSetup.route,
                            arguments = listOf(
                                navArgument("practiceType") { type = NavType.StringType }
                            )
                        ) {
                            val hskPracticeSetupViewModel =
                                koinViewModel<HSKPracticeSetupViewModel> {
                                    parametersOf(it.savedStateHandle)
                                }
                            HSKPracticeSetup(
                                navController = navController,
                                viewModel = hskPracticeSetupViewModel,
                            )
                        }

                        composable(
                            route = PracticeRoutes.FlashCardPractice.route,
                            arguments = listOf(
                                navArgument("wordSource") { type = NavType.StringType },
                                navArgument("wordIds") { type = NavType.StringType }
                            )
                        ) {
                            val wordSource = it.arguments?.getString("wordSource") ?: "CC"
                            val wordIdsString = it.arguments?.getString("wordIds") ?: ""
                            val wordIds = if (wordIdsString.isNotEmpty()) {
                                wordIdsString.split(",").map { id -> id.toLong() }
                            } else {
                                emptyList()
                            }

                            val flashCardViewModel = koinViewModel<FlashCardPracticeViewModel> {
                                parametersOf(it.savedStateHandle)
                            }

                            FlashCardPractice(
                                navController = navController,
                                viewModel = flashCardViewModel
                            )
                        }

                        composable(
                            route = PracticeRoutes.MultipleChoicePractice.route,
                            arguments = listOf(
                                navArgument("wordSource") { type = NavType.StringType },
                                navArgument("wordIds") { type = NavType.StringType }
                            )
                        ) {
                            val multipleChoiceViewModel =
                                koinViewModel<MultipleChoicePracticeViewModel> {
                                    parametersOf(it.savedStateHandle)
                                }

                            MultipleChoicePractice(
                                navController = navController,
                                viewModel = multipleChoiceViewModel
                            )
                        }

                        composable(
                            route = PracticeRoutes.ListeningPractice.route,
                            arguments = listOf(
                                navArgument("wordSource") { type = NavType.StringType },
                                navArgument("wordIds") { type = NavType.StringType }
                            )
                        ) {
                            val listeningViewModel = koinViewModel<ListeningPracticeViewModel> {
                                parametersOf(it.savedStateHandle)
                            }

                            ListeningPractice(
                                navController = navController,
                                viewModel = listeningViewModel
                            )
                        }

                        navigation(
                            startDestination = SettingsRoutes.Settings.route,
                            route = MainRoutes.Settings.name
                        ) {
                            composable(route = SettingsRoutes.Settings.route) {
                                val settingsViewModel = koinViewModel<SettingsViewModel>()
                                SettingsScreen(
                                    navController = navController,
                                    settingsViewModel = settingsViewModel,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

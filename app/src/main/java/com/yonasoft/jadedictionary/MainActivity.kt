package com.yonasoft.jadedictionary

import ListeningPractice
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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
import com.yonasoft.jadedictionary.core.utils.PlayStoreUtils
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
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TEST", "=== BASIC LOG TEST ===")
        Log.e("TEST", "=== ERROR LOG TEST ===")


        // Log all extras for debugging
        intent.extras?.let { extras ->
            for (key in extras.keySet()) {
                Log.d(TAG, "Extra: $key = ${extras.get(key)}")
            }
        }

        setContent {
            val context = LocalContext.current
            val themePreferences = remember { ThemePreferences(context) }
            val isDarkTheme by themePreferences.isDarkTheme.collectAsState(initial = false)

            JadeDictionaryTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    handleNotificationIntent(intent, navController)
                }

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

    // ONLY ONE handleNotificationIntent method - remove the other one completely
    private fun handleNotificationIntent(intent: android.content.Intent, navController: androidx.navigation.NavController?) {
        Log.d(TAG, "handleNotificationIntent called")
        Log.d(TAG, "Intent: $intent")
        Log.d(TAG, "Intent extras: ${intent.extras}")

        // Check for Play Store action FIRST
        val openPlayStore = intent.getBooleanExtra("open_play_store", false)
        val fromNotification = intent.getBooleanExtra("from_notification", false)

        Log.d(TAG, "open_play_store: $openPlayStore")
        Log.d(TAG, "from_notification: $fromNotification")

        if (openPlayStore) {
            Log.d(TAG, "Opening Play Store...")
            try {
                PlayStoreUtils.openRateApp(this)
                Log.d(TAG, "Play Store opened successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to open Play Store", e)
            }
            return // Don't continue with other navigation
        }

        // Handle share app
        if (intent.getBooleanExtra("share_app", false)) {
            Log.d(TAG, "Sharing app...")
            PlayStoreUtils.shareApp(this)
            return
        }

        // Handle navigation to practice
        val shouldNavigateToPractice = intent.getBooleanExtra("navigate_to_practice", false)
        if (shouldNavigateToPractice) {
            Log.d(TAG, "Navigating to practice...")
            navController?.navigate(MainRoutes.Practice.name) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = false
                }
            }
        }

        // Handle word detail navigation
        val navigateToWord = intent.getStringExtra("navigate_to_word")
        if (navigateToWord != null) {
            Log.d(TAG, "Navigating to word: $navigateToWord")
            val wordSource = intent.getStringExtra("word_source") ?: "CC"
            val route = if (wordSource == "HSK") {
                "word_detail_hsk/$navigateToWord"
            } else {
                "word_detail_cc/$navigateToWord"
            }
            navController?.navigate(route)
        }

        Log.d(TAG, "handleNotificationIntent completed")
    }
}
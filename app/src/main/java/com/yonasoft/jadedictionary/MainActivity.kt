package com.yonasoft.jadedictionary

import ListeningPractice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    private var pendingIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "MainActivity onCreate")
        Log.d(TAG, "Intent: $intent")
        Log.d(TAG, "Intent action: ${intent.action}")
        Log.d(TAG, "Intent data: ${intent.data}")

        // Store the intent for processing after UI is ready
        pendingIntent = intent

        // Log all extras for debugging
        intent.extras?.let { extras ->
            Log.d(TAG, "Intent has ${extras.size()} extras:")
            for (key in extras.keySet()) {
                Log.d(TAG, "Extra: $key = ${extras.get(key)}")
            }
        } ?: Log.d(TAG, "Intent has no extras")

        setContent {
            val context = LocalContext.current
            val themePreferences = remember { ThemePreferences(context) }
            val isDarkTheme by themePreferences.isDarkTheme.collectAsState(initial = false)
            val navController = rememberNavController()
            var hasProcessedIntent by remember { mutableStateOf(false) }

            JadeDictionaryTheme(darkTheme = isDarkTheme) {
                // Process intent after UI is fully rendered
                LaunchedEffect(navController) {
                    if (!hasProcessedIntent && pendingIntent != null) {
                        Log.d(TAG, "Processing pending intent...")
                        handleNotificationIntent(pendingIntent!!, navController)
                        hasProcessedIntent = true
                        pendingIntent = null
                    }
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "=== onNewIntent called ===")

        intent.let {
            Log.d(TAG, "New intent: $it")
            Log.d(TAG, "New intent action: ${it.action}")
            Log.d(TAG, "New intent data: ${it.data}")

            it.extras?.let { extras ->
                Log.d(TAG, "New intent has ${extras.size()} extras:")
                for (key in extras.keySet()) {
                    Log.d(TAG, "New intent extra: $key = ${extras.get(key)}")
                }
            } ?: Log.d(TAG, "New intent has no extras")

            setIntent(it)
            // Process the new intent immediately
            handleNotificationIntent(it, null)
        }
    }

    private fun handleNotificationIntent(
        intent: Intent,
        navController: androidx.navigation.NavController?
    ) {
        Log.d(TAG, "=== handleNotificationIntent called ===")
        Log.d(TAG, "Intent: $intent")
        Log.d(TAG, "Intent extras count: ${intent.extras?.size() ?: 0}")

        try {
            // Check for FCM data first (prefixed with fcm_)
            val fcmUrl = intent.getStringExtra("fcm_url")
            val fcmAction = intent.getStringExtra("fcm_action")

            Log.d(TAG, "FCM URL: $fcmUrl")
            Log.d(TAG, "FCM Action: $fcmAction")

            // Check for direct URL from campaign notification
            val urlExtra = intent.getStringExtra("url") ?: fcmUrl
            if (!urlExtra.isNullOrEmpty()) {
                Log.d(TAG, "Found URL in intent extras: $urlExtra")
                if (urlExtra.contains("play.google.com")) {
                    Log.d(TAG, "Opening Play Store from campaign...")
                    PlayStoreUtils.openRateApp(this)
                    Log.d(TAG, "Play Store opened successfully from campaign")
                    return
                }
            }

            // Check for Play Store action FIRST
            val openPlayStore = intent.getBooleanExtra("open_play_store", false) ||
                    fcmAction == "open_play_store" || fcmAction == "rate_app"
            val fromNotification = intent.getBooleanExtra("from_notification", false)

            Log.d(TAG, "open_play_store: $openPlayStore")
            Log.d(TAG, "from_notification: $fromNotification")

            if (openPlayStore) {
                Log.d(TAG, "Opening Play Store...")
                PlayStoreUtils.openRateApp(this)
                Log.d(TAG, "Play Store opened successfully")
                return // Don't continue with other navigation
            }

            // Handle share app
            if (intent.getBooleanExtra("share_app", false) || fcmAction == "share_app") {
                Log.d(TAG, "Sharing app...")
                PlayStoreUtils.shareApp(this)
                return
            }

            // Handle navigation to practice
            val shouldNavigateToPractice = intent.getBooleanExtra("navigate_to_practice", false) ||
                    fcmAction == "practice"
            if (shouldNavigateToPractice && navController != null) {
                Log.d(TAG, "Navigating to practice...")
                navController.navigate(MainRoutes.Practice.name) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                    }
                }
            }

            // Handle word detail navigation
            val navigateToWord = intent.getStringExtra("navigate_to_word") ?:
            intent.getStringExtra("fcm_word_id")
            if (navigateToWord != null && navController != null) {
                Log.d(TAG, "Navigating to word: $navigateToWord")
                val wordSource = intent.getStringExtra("word_source") ?:
                intent.getStringExtra("fcm_word_source") ?: "CC"
                val route = if (wordSource == "HSK") {
                    "word_detail_hsk/$navigateToWord"
                } else {
                    "word_detail_cc/$navigateToWord"
                }
                navController.navigate(route)
            }

            Log.d(TAG, "handleNotificationIntent completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error handling notification intent", e)
        }
    }
}
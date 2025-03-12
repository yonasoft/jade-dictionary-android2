package com.yonasoft.jadedictionary.features.word_search.presentation.screens

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.navigation.WordRoutes
import com.yonasoft.jadedictionary.core.words.presentation.components.CCWordColumn
import com.yonasoft.jadedictionary.features.shared.presentation.components.JadeTabRow
import com.yonasoft.jadedictionary.features.word_search.presentation.components.HandwritingInputBottomSheet
import com.yonasoft.jadedictionary.features.word_search.presentation.components.WordSearchAppBar
import com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels.WordSearchViewModel
import org.koin.androidx.compose.koinViewModel


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun WordSearch(
    navController: NavHostController,
    wordSearchViewModel: WordSearchViewModel = koinViewModel()
) {
    val inputTabs = listOf(
        ImageVector.vectorResource(R.drawable.baseline_keyboard_24),
        ImageVector.vectorResource(R.drawable.baseline_draw_24),
        ImageVector.vectorResource(R.drawable.outline_mic_24)
    )

    val focusRequester = wordSearchViewModel.focusRequester
    val focusManager = wordSearchViewModel.localFocusManager.current
    val keyboardController = wordSearchViewModel.localKeyboardController.current

    val searchQuery by wordSearchViewModel.searchQuery.collectAsStateWithLifecycle()
    val words by wordSearchViewModel.words.collectAsStateWithLifecycle()
    val selectedInputTab by wordSearchViewModel.selectedInputTab.collectAsStateWithLifecycle()
    val showHandwritingSheet by wordSearchViewModel.showHandwritingSheet.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            wordSearchViewModel.updateSearchQuery(result?.get(0) ?: "")
        }
    }

    LaunchedEffect(selectedInputTab) {
        focusRequester.requestFocus()
        kotlinx.coroutines.delay(100)
        when (selectedInputTab) {
            0 -> {
                keyboardController?.show()
                wordSearchViewModel.setShowHandwritingSheet(false)
            }

            1 -> {
                keyboardController?.hide()
                wordSearchViewModel.setShowHandwritingSheet(true)
            }

            2 -> {
                wordSearchViewModel.setShowHandwritingSheet(false)
                keyboardController?.hide()
                val supportedLanguages = arrayOf(
                    "zh-CN",  // Chinese (Simplified)
                    "zh-TW",  // Chinese (Traditional)
                    "en-US",  // English (United States)
                    "en-GB",  // English (United Kingdom)
                )
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN")
                    putExtra(
                        RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES,
                        supportedLanguages
                    )

                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Go on then, say something.")
                }
                launcher.launch(intent)
            }
        }
    }

    HandwritingInputBottomSheet(
        isVisible = showHandwritingSheet,
        onDismiss = {
            wordSearchViewModel.setShowHandwritingSheet(false)
            wordSearchViewModel.updateInputTab(0) // Switch back to keyboard
        },
        onCharacterDrawn = { points ->
            // This is where you'll handle the recognition logic
            // For now, just a placeholder to show integration
            // You can implement your recognition logic here
        }
    )

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            WordSearchAppBar(
                navigateUp = {
                    navController.navigateUp()
                },
                searchQuery = searchQuery,
                onCancel = {
                    if (searchQuery.isEmpty()) {
                        focusManager.clearFocus()
                    } else {
                        wordSearchViewModel.updateSearchQuery("")
                    }
                },
                onValueChange = { newQuery ->
                    wordSearchViewModel.updateSearchQuery(newQuery)
                },
                focusRequester = focusRequester
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValue)
        ) {
            JadeTabRow(
                selectedIndex = selectedInputTab,
            ) {
                inputTabs.forEachIndexed { index, icon ->
                    Tab(
                        selectedContentColor = Color.White,
                        content = {
                            Icon(
                                imageVector = icon,
                                contentDescription = "",
                                tint = if (selectedInputTab == index) CustomColor.GREEN01.color else Color.White,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(28.dp)
                                    .background(color = Color.Black),
                            )
                        },
                        selected = selectedInputTab == index,
                        onClick = {
                            wordSearchViewModel.updateInputTab(index)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (words.isNotEmpty()) {
                LazyColumn(
                    Modifier
                        .fillMaxWidth(),
                    state = rememberLazyListState()
                ) {
                    itemsIndexed(
                        words,
                        key = { _, word -> word.id!! },
                    ) { _, word ->
                        CCWordColumn(word = word, onClick = {
                            navController.navigate(WordRoutes.WordDetail.createRoute(word.id!!))
                        })
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
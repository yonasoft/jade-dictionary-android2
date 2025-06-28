package com.yonasoft.jadedictionary.features.word_search.presentation.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import com.yonasoft.jadedictionary.core.navigation.WordRoutes
import com.yonasoft.jadedictionary.features.handwriting.presentation.components.HandwritingInputBottomSheet
import com.yonasoft.jadedictionary.features.ocr.presentation.components.OCRBottomSheet
import com.yonasoft.jadedictionary.features.shared.presentation.components.JadeTabRowAlternative
import com.yonasoft.jadedictionary.features.word.presentation.components.CCWordItem
import com.yonasoft.jadedictionary.features.word_search.presentation.components.WordSearchAppBar
import com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels.WordSearchViewModel

@Composable
fun WordSearch(
    navController: NavHostController,
    wordSearchViewModel: WordSearchViewModel
) {
    // ViewModel state collection with lifecycle awareness for performance
    val searchState by wordSearchViewModel.searchState.collectAsStateWithLifecycle()
    val wordListsState by wordSearchViewModel.wordListsState.collectAsStateWithLifecycle()
    val inputMethodState by wordSearchViewModel.inputMethodState.collectAsStateWithLifecycle()
    val handwritingState by wordSearchViewModel.handwritingState.collectAsStateWithLifecycle()
    val ocrState by wordSearchViewModel.ocrState.collectAsStateWithLifecycle()
    val recognitionState by wordSearchViewModel.recognitionState.collectAsStateWithLifecycle()

    // Extract variables for better readability
    val searchQuery = searchState.query
    val searchResults = searchState.results
    val wordLists = wordListsState.myWordLists
    val selectedTab = inputMethodState.selectedTab
    val showHandwritingSheet = handwritingState.showSheet
    val suggestedWords = handwritingState.suggestedWords
    val resetCanvasSignal = handwritingState.resetCanvasSignal
    val showOCRSheet = ocrState.showSheet
    val ocrResults = ocrState.results
    val isRecognizing = recognitionState.isLoading

    // UI state
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = wordSearchViewModel.focusRequester
    val focusManager = wordSearchViewModel.localFocusManager.current
    val keyboardController = wordSearchViewModel.localKeyboardController.current

    // Speech recognition launcher
    val voiceRecognitionLauncher = rememberVoiceRecognitionLauncher(
        onResult = { result ->
            wordSearchViewModel.updateSearchQuery(result ?: "")
        }
    )

    // Side effects
    LaunchedEffect(searchResults) {
        if (searchResults.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    LaunchedEffect(selectedTab) {
        handleTabSelection(
            selectedTab = selectedTab,
            wordSearchViewModel = wordSearchViewModel,
            focusRequester = focusRequester,
            keyboardController = keyboardController,
            voiceRecognitionLauncher = voiceRecognitionLauncher
        )
    }

    // Bottom sheets
    HandwritingSheet(
        isVisible = showHandwritingSheet,
        isRecognizing = isRecognizing,
        resetCanvasSignal = resetCanvasSignal,
        suggestedWords = suggestedWords,
        onDismiss = {
            wordSearchViewModel.setShowHandwritingSheet(false)
            wordSearchViewModel.updateInputTab(0)
        },
        onCharacterDrawn = { points ->
            wordSearchViewModel.processHandwritingStrokes(points)
        },
        onSuggestionSelected = { suggestion ->
            wordSearchViewModel.updateSearchQuery(suggestion)
            wordSearchViewModel.resetHandwritingCanvas()
        }
    )

    OCRSheet(
        isVisible = showOCRSheet,
        isRecognizing = isRecognizing,
        recognizedText = ocrResults,
        onDismiss = {
            wordSearchViewModel.setShowOCRSheet(false)
            wordSearchViewModel.updateInputTab(0)
        },
        onOCRCompleted = { bitmap ->
            wordSearchViewModel.processOCRImage(bitmap)
        },
        onTextSelected = { text ->
            wordSearchViewModel.updateSearchQuery(text)
            wordSearchViewModel.resetOCRImage()
        }
    )

    // Main UI
    Scaffold(
        containerColor = Color(0xFF0A0A0A),
        snackbarHost = {
            CustomSnackbarHost(snackbarHostState = snackbarHostState)
        },
        topBar = {
            WordSearchAppBar(
                navigateUp = { navController.navigateUp() },
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
                    wordSearchViewModel.search(newQuery)
                },
                focusRequester = focusRequester,
                createNewWordList = { title, description ->
                    wordSearchViewModel.createNewWordList(title, description)
                },
                snackbarHostState = snackbarHostState
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValue)
        ) {
            // Input method tabs
            InputMethodTabs(
                selectedTab = selectedTab,
                onTabSelected = { wordSearchViewModel.updateInputTab(it) }
            )

            // Results count
            ResultsCounter(visible = searchResults.isNotEmpty(), count = searchResults.size)

            when {
                searchState.loading && searchQuery.isNotEmpty() -> {
                    LoadingIndicator()
                }
                searchResults.isEmpty() && searchQuery.isNotEmpty() -> {
                    EmptySearchResults()
                }
                searchResults.isNotEmpty() -> {
                    SearchResultsList(
                        searchResults = searchResults,
                        wordLists = wordLists,
                        listState = listState,
                        onWordClick = { wordId ->
                            navController.navigate(WordRoutes.CCWordDetail.createRoute(wordId))
                        },
                        onAddToWordList = { word, list ->
                            wordSearchViewModel.addWordToWordList(word, list)
                        },
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

/**
 * Input method tab row
 */
@Composable
private fun InputMethodTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val inputTabs = listOf(
        ImageVector.vectorResource(R.drawable.baseline_keyboard_24) to "Keyboard",
        ImageVector.vectorResource(R.drawable.baseline_draw_24) to "Handwriting",
        ImageVector.vectorResource(R.drawable.outline_mic_24) to "Voice",
        ImageVector.vectorResource(R.drawable.outline_document_scanner_24) to "OCR Scanner",
    )

    JadeTabRowAlternative(
        selectedIndex = selectedTab,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        inputTabs.forEachIndexed { index, (icon, description) ->
            Tab(
                selectedContentColor = CustomColor.GREEN01.color,
                unselectedContentColor = Color.White.copy(alpha = 0.6f),
                content = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        // Circle background for selected tab
                        if (selectedTab == index) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(CustomColor.GREEN01.color.copy(alpha = 0.15f))
                            )
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = description,
                            tint = if (selectedTab == index) CustomColor.GREEN01.color else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                selected = selectedTab == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}

/**
 * Results counter component
 */
@Composable
private fun ResultsCounter(visible: Boolean, count: Int) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "$count result${if (count != 1) "s" else ""} found",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.3.sp
            )
        }
    }
}

/**
 * Empty search results state
 */
@Composable
private fun EmptySearchResults() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "No results",
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No results found",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 0.3.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Try a different search term or input method",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Search results list
 */
@Composable
private fun SearchResultsList(
    searchResults: List<com.yonasoft.jadedictionary.features.word.domain.cc.CCWord>,
    wordLists: List<com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onWordClick: (Long) -> Unit,
    onAddToWordList: (com.yonasoft.jadedictionary.features.word.domain.cc.CCWord, com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        itemsIndexed(
            searchResults,
            key = { _, word -> word.id!! }
        ) { _, word ->
            CCWordItem(
                word = word,
                onClick = { onWordClick(word.id!!) },
                wordLists = wordLists,
                onAddToWordList = { selectedWord, selectedList ->
                    onAddToWordList(selectedWord, selectedList)
                },
                snackbarHostState = snackbarHostState
            )
        }

        // Add space at bottom for better UX
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Handwriting bottom sheet wrapper
 */
@Composable
private fun HandwritingSheet(
    isVisible: Boolean,
    isRecognizing: Boolean,
    resetCanvasSignal: Long,
    suggestedWords: List<String>,
    onDismiss: () -> Unit,
    onCharacterDrawn: (List<androidx.compose.ui.geometry.Offset>) -> Unit,
    onSuggestionSelected: (String) -> Unit
) {
    HandwritingInputBottomSheet(
        isVisible = isVisible,
        isRecognizing = isRecognizing,
        resetCanvasSignal = resetCanvasSignal,
        onDismiss = onDismiss,
        onCharacterDrawn = onCharacterDrawn,
        suggestedWords = suggestedWords,
        onSuggestionSelected = onSuggestionSelected
    )
}

/**
 * OCR bottom sheet wrapper
 */
@Composable
private fun OCRSheet(
    isVisible: Boolean,
    isRecognizing: Boolean,
    recognizedText: List<String>,
    onDismiss: () -> Unit,
    onOCRCompleted: (android.graphics.Bitmap) -> Unit,
    onTextSelected: (String) -> Unit
) {
    OCRBottomSheet(
        isVisible = isVisible,
        onDismiss = onDismiss,
        onOCRCompleted = onOCRCompleted,
        recognizedText = recognizedText,
        isRecognizing = isRecognizing,
        onTextSelected = onTextSelected
    )
}

/**
 * Custom snackbar host
 */
@Composable
private fun CustomSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(16.dp)
    ) { snackbarData ->
        Snackbar(
            containerColor = Color(0xFF303030),
            contentColor = Color.White,
            actionContentColor = CustomColor.GREEN01.color,
            dismissActionContentColor = CustomColor.GREEN01.color,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = snackbarData.visuals.message,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * Voice recognition launcher helper
 */
@Composable
private fun rememberVoiceRecognitionLauncher(
    onResult: (String?) -> Unit
): androidx.activity.result.ActivityResultLauncher<Intent> {
    return rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val recognizedText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            onResult(recognizedText)
        }
    }
}

/**
 * Handle tab selection logic
 */
private suspend fun handleTabSelection(
    selectedTab: Int,
    wordSearchViewModel: WordSearchViewModel,
    focusRequester: androidx.compose.ui.focus.FocusRequester,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
    voiceRecognitionLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
) {
    kotlinx.coroutines.delay(100)

    when (selectedTab) {
        0 -> { // Keyboard
            focusRequester.requestFocus()
            keyboardController?.show()
        }
        1 -> { // Handwriting
            keyboardController?.hide()
        }
        2 -> { // Voice
            keyboardController?.hide()
            launchVoiceRecognition(voiceRecognitionLauncher)
        }
        3 -> { // OCR
            keyboardController?.hide()
        }
    }
}

/**
 * Launch voice recognition intent
 */
private fun launchVoiceRecognition(
    launcher: androidx.activity.result.ActivityResultLauncher<Intent>
) {
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
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search...")
    }

    launcher.launch(intent)
}

@Composable
private fun LoadingIndicator() {
    Box(
        Modifier.fillMaxWidth().padding(top = 16.dp), Alignment.Center
    ) {
        CircularProgressIndicator(
            color = CustomColor.GREEN01.color,
            modifier = Modifier.size(36.dp),
            strokeWidth = 3.dp
        )
    }
}
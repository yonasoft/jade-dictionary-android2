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
    val inputTabs = listOf(
        ImageVector.vectorResource(R.drawable.baseline_keyboard_24),
        ImageVector.vectorResource(R.drawable.baseline_draw_24),
        ImageVector.vectorResource(R.drawable.outline_mic_24),
        ImageVector.vectorResource(R.drawable.outline_document_scanner_24),
    )

    val focusRequester = wordSearchViewModel.focusRequester
    val focusManager = wordSearchViewModel.localFocusManager.current
    val keyboardController = wordSearchViewModel.localKeyboardController.current

    val searchState by wordSearchViewModel.searchState.collectAsStateWithLifecycle()
    val wordListsState by wordSearchViewModel.wordListsState.collectAsStateWithLifecycle()
    val inputMethodState by wordSearchViewModel.inputMethodState.collectAsStateWithLifecycle()
    val handwritingState by wordSearchViewModel.handwritingState.collectAsStateWithLifecycle()
    val ocrState by wordSearchViewModel.ocrState.collectAsStateWithLifecycle()
    val recognitionState by wordSearchViewModel.recognitionState.collectAsStateWithLifecycle()

    // Now you can use the bundled states in your UI
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

    val listState =rememberLazyListState()

    // Create a SnackbarHostState that will be used by both the Scaffold and the AppBar
    val snackbarHostState = remember { SnackbarHostState() }

    // Update background color to be darker for better contrast
    val backgroundColor = Color(0xFF0A0A0A)

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                wordSearchViewModel.updateSearchQuery(result?.get(0) ?: "")
            }
        }

    LaunchedEffect(searchResults) {
        listState.scrollToItem(0)
    }

    LaunchedEffect(selectedTab) {
        kotlinx.coroutines.delay(100)
        when (selectedTab) {
            0 -> { // Keyboard
                focusRequester.requestFocus()
                keyboardController?.show()
                wordSearchViewModel.setShowHandwritingSheet(false)
                wordSearchViewModel.setShowOCRSheet(false)
            }
            1 -> { // Handwriting
                keyboardController?.hide()
                wordSearchViewModel.setShowHandwritingSheet(true)
                wordSearchViewModel.setShowOCRSheet(false)
            }
            2 -> { // Voice
                wordSearchViewModel.setShowHandwritingSheet(false)
                wordSearchViewModel.setShowOCRSheet(false)
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
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search...")
                }
                launcher.launch(intent)
            }
            3 -> { // OCR
                keyboardController?.hide()
                wordSearchViewModel.setShowHandwritingSheet(false)
                wordSearchViewModel.setShowOCRSheet(true)
            }
        }
    }

    // Handwriting bottom sheet
    HandwritingInputBottomSheet(
        isVisible = showHandwritingSheet,
        isRecognizing = isRecognizing,
        resetCanvasSignal = resetCanvasSignal,
        onDismiss = {
            wordSearchViewModel.setShowHandwritingSheet(false)
            wordSearchViewModel.updateInputTab(0)
        },
        onCharacterDrawn = { points ->
            wordSearchViewModel.processHandwritingStrokes(points)
        },
        suggestedWords = suggestedWords,
        onSuggestionSelected = { suggestion ->
            wordSearchViewModel.updateSearchQuery(suggestion)
            // Don't close the sheet or change input tab, just reset the canvas
            wordSearchViewModel.resetHandwritingCanvas()
        }
    )

    // OCR bottom sheet
    OCRBottomSheet(
        isVisible = showOCRSheet,
        onDismiss = {
            wordSearchViewModel.setShowOCRSheet(false)
            wordSearchViewModel.updateInputTab(0)
        },
        onOCRCompleted = { bitmap ->
            wordSearchViewModel.processOCRImage(bitmap)
        },
        recognizedText = ocrResults,
        isRecognizing = isRecognizing,
        onTextSelected = { text ->
            wordSearchViewModel.updateSearchQuery(text)
            wordSearchViewModel.resetOCRImage()
        }
    )

    Scaffold(
        containerColor = backgroundColor,
        // Use the shared snackbarHostState in the Scaffold
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            ) { snackbarData ->
                // Custom snackbar appearance
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
        },
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
                focusRequester = focusRequester,
                createNewWordList = { title, description ->
                    wordSearchViewModel.createNewWordList(title, description)
                },
                // Pass the shared snackbarHostState to the app bar
                snackbarHostState = snackbarHostState
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValue)
        ) {
            // Enhanced Tab Row
            JadeTabRowAlternative(
                selectedIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                inputTabs.forEachIndexed { index, icon ->
                    Tab(
                        selectedContentColor = CustomColor.GREEN01.color,
                        unselectedContentColor = Color.White.copy(alpha = 0.6f),
                        content = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
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
                                    contentDescription = when (index) {
                                        0 -> "Keyboard"
                                        1 -> "Handwriting"
                                        2 -> "Voice"
                                        3 -> "OCR Scanner"
                                        else -> ""
                                    },
                                    tint = if (selectedTab == index) CustomColor.GREEN01.color else Color.White.copy(
                                        alpha = 0.6f
                                    ),
                                    modifier = Modifier
                                        .size(24.dp)
                                )
                            }
                        },
                        selected = selectedTab == index,
                        onClick = {
                            wordSearchViewModel.updateInputTab(index)
                        }
                    )
                }
            }

            // Results count with improved styling
            AnimatedVisibility(
                visible = searchResults.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${searchResults.size} result${if (searchResults.size != 1) "s" else ""} found",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            // Empty state with improved styling
            AnimatedVisibility(
                visible = searchResults.isEmpty() && searchQuery.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
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
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            if (searchResults.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    itemsIndexed(
                        searchResults,
                        key = { _, word -> word.id!! },
                    ) { _, word ->
                        CCWordItem(
                            word = word,
                            onClick = {
                                navController.navigate(WordRoutes.WordDetail.createRoute(word.id!!))
                            },
                            wordLists = wordLists,
                            onAddToWordList = { selectedWord, selectedList ->
                                wordSearchViewModel.addWordToWordList(selectedWord, selectedList)
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
        }
    }
}
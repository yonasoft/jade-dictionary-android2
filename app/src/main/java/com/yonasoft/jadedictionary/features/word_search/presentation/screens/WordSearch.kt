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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.yonasoft.jadedictionary.core.words.presentation.components.CCWordColumn
import com.yonasoft.jadedictionary.features.handwriting.presentation.components.HandwritingInputBottomSheet
import com.yonasoft.jadedictionary.features.ocr.presentation.components.OCRBottomSheet
import com.yonasoft.jadedictionary.features.shared.presentation.components.JadeTabRowAlternative
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

    val searchQuery by wordSearchViewModel.searchQuery.collectAsStateWithLifecycle()
    val words by wordSearchViewModel.words.collectAsStateWithLifecycle()
    val selectedInputTab by wordSearchViewModel.selectedInputTab.collectAsStateWithLifecycle()

    // Handwriting states
    val showHandwritingSheet by wordSearchViewModel.showHandwritingSheet.collectAsStateWithLifecycle()
    val suggestedWords by wordSearchViewModel.suggestedWords.collectAsStateWithLifecycle()
    val recognitionLoading by wordSearchViewModel.recognitionLoading.collectAsStateWithLifecycle()
    val resetCanvasSignal by wordSearchViewModel.resetCanvasSignal.collectAsStateWithLifecycle()

    // OCR states
    val showOCRSheet by wordSearchViewModel.showOCRSheet.collectAsStateWithLifecycle()
    val ocrResults by wordSearchViewModel.ocrResults.collectAsStateWithLifecycle()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                wordSearchViewModel.updateSearchQuery(result?.get(0) ?: "")
            }
        }

    LaunchedEffect(selectedInputTab) {
        kotlinx.coroutines.delay(100)
        when (selectedInputTab) {
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
        isRecognizing = recognitionLoading,
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
        isRecognizing = recognitionLoading,
        onTextSelected = { text ->
            wordSearchViewModel.updateSearchQuery(text)
            wordSearchViewModel.resetOCRImage()
        }
    )

    Scaffold(
        containerColor = Color(0xFF121212),
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
            // Enhanced Tab Row
            JadeTabRowAlternative(
                selectedIndex = selectedInputTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                inputTabs.forEachIndexed { index, icon ->
                    Tab(
                        selectedContentColor = CustomColor.GREEN01.color,
                        unselectedContentColor = Color.White.copy(alpha = 0.7f),
                        content = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                            ) {
                                // Circle background for selected tab
                                if (selectedInputTab == index) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(CustomColor.GREEN01.color.copy(alpha = 0.15f))
                                    )
                                }

                                Icon(
                                    imageVector = icon,
                                    contentDescription = when(index) {
                                        0 -> "Keyboard"
                                        1 -> "Handwriting"
                                        2 -> "Voice"
                                        3 -> "OCR Scanner"
                                        else -> ""
                                    },
                                    tint = if (selectedInputTab == index) CustomColor.GREEN01.color else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(28.dp)
                                )
                            }
                        },
                        selected = selectedInputTab == index,
                        onClick = {
                            wordSearchViewModel.updateInputTab(index)
                        }
                    )
                }
            }

            // Results count
            AnimatedVisibility(
                visible = words.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${words.size} result${if (words.size != 1) "s" else ""} found",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            // Empty state
            AnimatedVisibility(
                visible = words.isEmpty() && searchQuery.isNotEmpty(),
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
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No results found",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Try a different search term or input method",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Word list
            if (words.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = rememberLazyListState()
                ) {
                    itemsIndexed(
                        words,
                        key = { _, word -> word.id!! },
                    ) { _, word ->
                        CCWordColumn(word = word, onClick = {
                            navController.navigate(WordRoutes.WordDetail.createRoute(word.id!!))
                        })
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
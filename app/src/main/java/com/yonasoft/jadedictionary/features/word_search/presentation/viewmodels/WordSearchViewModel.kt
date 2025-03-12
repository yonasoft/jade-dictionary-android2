package com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.core.words.data.cc.CCWord
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.features.handwriting.domain.services.HandwritingRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordSearchViewModel(
    application: Application,
    private val repository: CCWordRepository
) : AndroidViewModel(application) {

    private val _searchQuery = MutableStateFlow("")
    private val _words = MutableStateFlow<List<CCWord>>(emptyList())
    private val _selectedInputTab = MutableStateFlow(0)
    private val _suggestedWords = MutableStateFlow<List<String>>(emptyList())
    private val _showHandwritingSheet = MutableStateFlow(false)
    private val _recognitionLoading = MutableStateFlow(false)
    private val _resetCanvasSignal = MutableStateFlow(0L)


    val searchQuery: StateFlow<String> = _searchQuery
    val words: StateFlow<List<CCWord>> = _words
    val selectedInputTab: StateFlow<Int> = _selectedInputTab
    val suggestedWords: StateFlow<List<String>> = _suggestedWords.asStateFlow()
    val showHandwritingSheet: StateFlow<Boolean> = _showHandwritingSheet.asStateFlow()
    val recognitionLoading: StateFlow<Boolean> = _recognitionLoading.asStateFlow()
    val resetCanvasSignal: StateFlow<Long> = _resetCanvasSignal.asStateFlow()

    val focusRequester = FocusRequester()
    val localFocusManager = LocalFocusManager
    val localKeyboardController = LocalSoftwareKeyboardController

    // Handwriting recognizer
    private val handwritingRecognizer = HandwritingRecognizer(application.applicationContext)

    // Store strokes for recognition
    private val currentStrokes = mutableListOf<List<Offset>>()

    init {
        viewModelScope.launch(Dispatchers.Main) {
            focusRequester.requestFocus()
            _searchQuery.collectLatest {
                search(it)
            }
        }

        // Initialize handwriting recognizer
        viewModelScope.launch(Dispatchers.IO) {
            try {
                handwritingRecognizer.initialize()
            } catch (e: Exception) {
                Log.e("WordSearchViewModel", "Failed to initialize handwriting recognizer", e)
            }
        }
    }

    fun updateSearchQuery(newValue: String) {
        _searchQuery.value = newValue
    }

    fun updateInputTab(index: Int) {
        _selectedInputTab.value = index

        // Clear suggestions when switching away from handwriting
        if (index != 1) {
            _suggestedWords.value = emptyList()
            currentStrokes.clear()
        }
    }

    fun setShowHandwritingSheet(boolean: Boolean) {
        _showHandwritingSheet.value = boolean

        // Clear suggestions when hiding the sheet
        if (!boolean) {
            _suggestedWords.value = emptyList()
            currentStrokes.clear()
        }
    }

    fun processHandwritingStrokes(points: List<Offset>) {
        if (points.isEmpty()) {
            // Clear data if empty points (clear button was pressed)
            currentStrokes.clear()
            _suggestedWords.value = emptyList()
            return
        }

        // Add the stroke to our collection
        currentStrokes.add(points)

        // Perform recognition
        recognizeHandwriting()
    }

    private fun recognizeHandwriting() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _recognitionLoading.value = true

                // Wait until we have at least 1 stroke
                if (currentStrokes.isNotEmpty()) {
                    val results = handwritingRecognizer.recognizeHandwriting(currentStrokes)

                    withContext(Dispatchers.Main) {
                        if (results.isNotEmpty()) {
                            _suggestedWords.value = results
                        } else {
                            // Provide some fallback suggestions if recognition fails
                            _suggestedWords.value = listOf("你", "我", "的", "是", "了")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("WordSearchViewModel", "Handwriting recognition failed", e)
                // Fallback to some default suggestions if recognition fails
                withContext(Dispatchers.Main) {
                    _suggestedWords.value = listOf("你", "好", "的", "是", "了")
                }
            } finally {
                _recognitionLoading.value = false
            }
        }
    }

    suspend fun search(query: String) {
        withContext(Dispatchers.IO) {
            try {
                delay(150)
                val result = repository.searchWords(query)
                withContext(Dispatchers.Main) {
                    _words.value = result
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Search failed", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        handwritingRecognizer.close()
    }

    fun resetHandwritingCanvas() {
        // Clear current strokes to reset the canvas
        currentStrokes.clear()
        _suggestedWords.value = emptyList()

        // Signal to the UI to clear the canvas
        // This will be picked up by the bottom sheet through the Flow
        _resetCanvasSignal.value = System.currentTimeMillis()
    }
}
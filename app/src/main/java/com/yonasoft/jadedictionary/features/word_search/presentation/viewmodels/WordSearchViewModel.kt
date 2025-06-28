package com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.features.handwriting.domain.services.HandwritingRecognizer
import com.yonasoft.jadedictionary.features.handwriting.presentation.state.HandwritingState
import com.yonasoft.jadedictionary.features.handwriting.presentation.state.RecognitionState
import com.yonasoft.jadedictionary.features.ocr.domain.services.OCRService
import com.yonasoft.jadedictionary.features.ocr.presentation.state.OCRState
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordListRepository
import com.yonasoft.jadedictionary.features.word_lists.presentation.state.WordListsState
import com.yonasoft.jadedictionary.features.word_search.presentation.state.InputMethodState
import com.yonasoft.jadedictionary.features.word_search.presentation.state.SearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for the Word Search screen that handles searching words,
 * managing word lists, and various input methods (keyboard, handwriting, voice, OCR).
 */
@OptIn(FlowPreview::class)
class WordSearchViewModel(
    application: Application,
    private val ccWordRepository: CCWordRepository,
    private val wordListsRepository: CCWordListRepository
) : AndroidViewModel(application) {

    // ======== StateFlows ========
    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _wordListsState = MutableStateFlow(WordListsState())
    val wordListsState: StateFlow<WordListsState> = _wordListsState.asStateFlow()

    private val _inputMethodState = MutableStateFlow(InputMethodState())
    val inputMethodState: StateFlow<InputMethodState> = _inputMethodState.asStateFlow()

    private val _handwritingState = MutableStateFlow(HandwritingState())
    val handwritingState: StateFlow<HandwritingState> = _handwritingState.asStateFlow()

    private val _ocrState = MutableStateFlow(OCRState())
    val ocrState: StateFlow<OCRState> = _ocrState.asStateFlow()

    private val _recognitionState = MutableStateFlow(RecognitionState())
    val recognitionState: StateFlow<RecognitionState> = _recognitionState.asStateFlow()

    // Focus handling
    val focusRequester = FocusRequester()
    val localFocusManager = LocalFocusManager
    val localKeyboardController = LocalSoftwareKeyboardController

    // Service objects
    private val handwritingRecognizer = HandwritingRecognizer()
    private val ocrService = OCRService(application.applicationContext)

    // Store strokes for recognition
    private val currentStrokes = mutableListOf<List<Offset>>()

    // Search debounce job
    private var searchJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.Main) {
            focusRequester.requestFocus()
            handwritingRecognizer.initialize()
        }

        // Load word lists at initialization
        loadWordLists()
    }

    // ======== Search Functions ========

    fun updateSearchQuery(newValue: String) {
        _searchState.update { it.copy(query = newValue) }
    }

    fun search(query: String) {
        // Cancel previous search job if it's still running
        searchJob?.cancel()

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                _searchState.update {
                    it.copy(
                        loading = true
                    )
                }
                val result = ccWordRepository.searchWords(query)
                withContext(Dispatchers.Main) {
                    _searchState.update {
                        it.copy(
                            results = result,
                            loading = false,
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Search failed", e)
            }
        }
    }

    // ======== Input Tab Functions ========

    fun updateInputTab(index: Int) {
        if (_inputMethodState.value.selectedTab == index) return

        _inputMethodState.update { it.copy(selectedTab = index) }

        // Reset all input methods first
        resetInputMethods()

        // Then activate the selected input method
        when (index) {
            INPUT_TAB_KEYBOARD -> { /* Default state, nothing to do */ }
            INPUT_TAB_HANDWRITING -> setShowHandwritingSheet(true)
            INPUT_TAB_VOICE -> { /* Handled in UI by launching intent */ }
            INPUT_TAB_OCR -> setShowOCRSheet(true)
        }
    }

    private fun resetInputMethods() {
        setShowHandwritingSheet(false)
        setShowOCRSheet(false)
        _handwritingState.update { it.copy(suggestedWords = emptyList()) }
        currentStrokes.clear()
        _ocrState.update { it.copy(results = emptyList(), currentImage = null) }
    }

    // ======== Handwriting Functions ========

    fun setShowHandwritingSheet(show: Boolean) {
        _handwritingState.update { it.copy(showSheet = show) }

        // Clear suggestions when hiding the sheet
        if (!show) {
            _handwritingState.update { it.copy(suggestedWords = emptyList()) }
            currentStrokes.clear()
        }
    }

    fun processHandwritingStrokes(points: List<Offset>) {
        if (points.isEmpty()) {
            // Clear data if empty points (clear button was pressed)
            currentStrokes.clear()
            _handwritingState.update { it.copy(suggestedWords = emptyList()) }
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
                _recognitionState.update { it.copy(isLoading = true) }

                if (currentStrokes.isNotEmpty()) {
                    val results = handwritingRecognizer.recognizeHandwriting(currentStrokes)

                    withContext(Dispatchers.Main) {
                        _handwritingState.update { it ->
                            it.copy(
                                suggestedWords = results.takeIf { it.isNotEmpty() }
                                    ?: DEFAULT_HANDWRITING_SUGGESTIONS
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Handwriting recognition failed", e)
                withContext(Dispatchers.Main) {
                    _handwritingState.update {
                        it.copy(suggestedWords = DEFAULT_HANDWRITING_SUGGESTIONS)
                    }
                }
            } finally {
                _recognitionState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun resetHandwritingCanvas() {
        // Clear current strokes to reset the canvas
        currentStrokes.clear()
        _handwritingState.update {
            it.copy(
                suggestedWords = emptyList(),
                resetCanvasSignal = System.currentTimeMillis()
            )
        }
    }

    // ======== OCR Functions ========

    fun setShowOCRSheet(show: Boolean) {
        _ocrState.update { it.copy(showSheet = show) }

        // Clear OCR results when hiding the sheet
        if (!show) {
            _ocrState.update { it.copy(results = emptyList(), currentImage = null) }
        }
    }

    fun processOCRImage(bitmap: Bitmap) {
        performOCR { ocrService.recognizeText(bitmap) }
        _ocrState.update { it.copy(currentImage = bitmap) }
    }

    fun processOCRImage(uri: Uri) {
        performOCR { ocrService.recognizeText(uri) }
    }

    private fun performOCR(recognizeFunction: suspend () -> List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _recognitionState.update { it.copy(isLoading = true) }

                val results = recognizeFunction()

                withContext(Dispatchers.Main) {
                    _ocrState.update { it.copy(results = results) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "OCR failed", e)
                withContext(Dispatchers.Main) {
                    _ocrState.update { it.copy(results = emptyList()) }
                }
            } finally {
                _recognitionState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun resetOCRImage() {
        _ocrState.update { it.copy(currentImage = null, results = emptyList()) }
    }

    // ======== Word List Functions ========

    private fun loadWordLists() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lists = wordListsRepository.getAllWordLists()
                withContext(Dispatchers.Main) {
                    _wordListsState.update { it.copy(myWordLists = lists) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load word lists", e)
            }
        }
    }

    fun addWordToWordList(word: CCWord, wordList: CCWordList) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Check if word is already in the list
                val existingIds = wordList.wordIds.toMutableList()

                // Only add if not already in list
                if (word.id != null && !existingIds.contains(word.id)) {
                    existingIds.add(word.id)

                    // Update the word list with new wordIds and count
                    val updatedList = wordList.copy(
                        wordIds = existingIds,
                        numberOfWords = existingIds.size.toLong(),
                        updatedAt = System.currentTimeMillis()
                    )

                    // Update in database
                    wordListsRepository.updateWordList(updatedList)

                    // Refresh word lists
                    loadWordLists()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add word to list", e)
            }
        }
    }

    fun createNewWordList(title: String, description: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newWordList = CCWordList(
                    id = null, // Let Room generate the ID
                    title = title,
                    description = description ?: "",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    numberOfWords = 0,
                    wordIds = emptyList()
                )

                wordListsRepository.insertWordList(newWordList)

                // Refresh the word lists
                loadWordLists()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create word list", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        handwritingRecognizer.close()
        ocrService.close()
        searchJob?.cancel()
    }

    companion object {
        private const val TAG = "WordSearchViewModel"

        // Input tab indices
        const val INPUT_TAB_KEYBOARD = 0
        const val INPUT_TAB_HANDWRITING = 1
        const val INPUT_TAB_VOICE = 2
        const val INPUT_TAB_OCR = 3

        // Default suggestions if recognition fails
        private val DEFAULT_HANDWRITING_SUGGESTIONS = listOf("你", "我", "的", "是", "了")
    }
}
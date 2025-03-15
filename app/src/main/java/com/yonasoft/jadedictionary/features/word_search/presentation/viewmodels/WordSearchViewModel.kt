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
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.features.handwriting.domain.services.HandwritingRecognizer
import com.yonasoft.jadedictionary.features.ocr.domain.services.OCRService
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordListRepository
import com.yonasoft.jadedictionary.features.word_lists.presentation.state.WordListsState
import com.yonasoft.jadedictionary.features.handwriting.presentation.state.HandwritingState
import com.yonasoft.jadedictionary.features.word_search.presentation.state.InputMethodState
import com.yonasoft.jadedictionary.features.ocr.presentation.state.OCRState
import com.yonasoft.jadedictionary.features.handwriting.presentation.state.RecognitionState
import com.yonasoft.jadedictionary.features.word_search.presentation.state.SearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordSearchViewModel(
    application: Application,
    private val ccWordRepository: CCWordRepository,
    private val wordListsRepository: CCWordListRepository
) : AndroidViewModel(application) {

    // ======== StateFlows for Each Group ========

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

    // Utility objects
    val focusRequester = FocusRequester()
    val localFocusManager = LocalFocusManager
    val localKeyboardController = LocalSoftwareKeyboardController

    // Service objects
    private val handwritingRecognizer = HandwritingRecognizer()
    private val ocrService = OCRService(application.applicationContext)

    // Store strokes for recognition
    private val currentStrokes = mutableListOf<List<Offset>>()

    init {
        viewModelScope.launch(Dispatchers.Main) {
            focusRequester.requestFocus()
            _searchState.collectLatest {
                search(it.query)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                handwritingRecognizer.initialize()
            } catch (e: Exception) {
                Log.e("WordSearchViewModel", "Failed to initialize handwriting recognizer", e)
            }
        }

        loadWordLists()
    }

    // ======== Search Functions ========

    fun updateSearchQuery(newValue: String) {
        _searchState.update { it.copy(query = newValue) }
    }

    suspend fun search(query: String) {
        withContext(Dispatchers.IO) {
            try {
                delay(150)
                val result = ccWordRepository.searchWords(query)
                withContext(Dispatchers.Main) {
                    _searchState.update { it.copy(results = result) }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Search failed", e)
            }
        }
    }

    // ======== Input Tab Functions ========

    fun updateInputTab(index: Int) {
        _inputMethodState.update { it.copy(selectedTab = index) }

        // Update visible sheet and reset data based on selected tab
        when (index) {
            0 -> { // Keyboard
                setShowHandwritingSheet(false)
                setShowOCRSheet(false)
                _handwritingState.update { it.copy(suggestedWords = emptyList()) }
                currentStrokes.clear()
            }
            1 -> { // Handwriting
                setShowHandwritingSheet(true)
                setShowOCRSheet(false)
                _handwritingState.update { it.copy(suggestedWords = emptyList()) }
                currentStrokes.clear()
            }
            2 -> { // Voice
                setShowHandwritingSheet(false)
                setShowOCRSheet(false)
                _handwritingState.update { it.copy(suggestedWords = emptyList()) }
                currentStrokes.clear()
            }
            3 -> { // OCR
                setShowHandwritingSheet(false)
                setShowOCRSheet(true)
                _ocrState.update { it.copy(results = emptyList(), currentImage = null) }
            }
        }
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

                // Wait until we have at least 1 stroke
                if (currentStrokes.isNotEmpty()) {
                    val results = handwritingRecognizer.recognizeHandwriting(currentStrokes)

                    withContext(Dispatchers.Main) {
                        if (results.isNotEmpty()) {
                            _handwritingState.update { it.copy(suggestedWords = results) }
                        } else {
                            // Provide some fallback suggestions if recognition fails
                            _handwritingState.update {
                                it.copy(suggestedWords = listOf("你", "我", "的", "是", "了"))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("WordSearchViewModel", "Handwriting recognition failed", e)
                // Fallback to some default suggestions if recognition fails
                withContext(Dispatchers.Main) {
                    _handwritingState.update {
                        it.copy(suggestedWords = listOf("你", "好", "的", "是", "了"))
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _recognitionState.update { it.copy(isLoading = true) }
                _ocrState.update { it.copy(currentImage = bitmap) }

                // Perform OCR on the image
                val results = ocrService.recognizeText(bitmap)

                withContext(Dispatchers.Main) {
                    if (results.isNotEmpty()) {
                        _ocrState.update { it.copy(results = results) }
                    } else {
                        // If no results, show empty state
                        _ocrState.update { it.copy(results = emptyList()) }
                    }
                }
            } catch (e: Exception) {
                Log.e("WordSearchViewModel", "OCR failed", e)
                withContext(Dispatchers.Main) {
                    _ocrState.update { it.copy(results = emptyList()) }
                }
            } finally {
                _recognitionState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Process image from URI for OCR
    fun processOCRImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _recognitionState.update { it.copy(isLoading = true) }

                // Perform OCR on the image URI
                val results = ocrService.recognizeText(uri)

                withContext(Dispatchers.Main) {
                    if (results.isNotEmpty()) {
                        _ocrState.update { it.copy(results = results) }
                    } else {
                        // If no results, show empty state
                        _ocrState.update { it.copy(results = emptyList()) }
                    }
                }
            } catch (e: Exception) {
                Log.e("WordSearchViewModel", "OCR failed", e)
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

    // Load all word lists from repository
    private fun loadWordLists() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lists = wordListsRepository.getAllWordLists()
                withContext(Dispatchers.Main) {
                    _wordListsState.update {  it.copy(myWordLists = lists)}
                }
            } catch (e: Exception) {
                Log.e("WordSearchViewModel", "Failed to load word lists", e)
            }
        }
    }

    // Add word to word list function
    fun addWordToWordList(word: CCWord, wordList: CCWordList) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Check if word is already in the list
                val existingIds = wordList.wordIds.toMutableList()

                // Only add if not already in list
                if (!existingIds.contains(word.id)) {
                    existingIds.add(word.id!!)

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
                Log.e("WordSearchViewModel", "Failed to add word to list", e)
            }
        }
    }

    // ======== Search Functions ========
    // ... existing functions ...

    // ======== Word List Functions ========
    fun createNewWordList(title: String, description: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val newWordList = CCWordList(
                id = null, // Let Room generate the ID
                title = title,
                description = description ?: "",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                numberOfWords = 0,
                wordIds = emptyList()
            )

            val insertedId = wordListsRepository.insertWordList(newWordList)

            // Refresh the word lists
            loadWordLists()
        }
    }

    override fun onCleared() {
        super.onCleared()
        handwritingRecognizer.close()
        ocrService.close()
    }
}
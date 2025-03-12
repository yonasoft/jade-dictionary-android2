package com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels

import android.util.Log
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.core.words.data.cc.CCWord
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordSearchViewModel(private val repository: CCWordRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _words = MutableStateFlow<List<CCWord>>(emptyList())
    private val _selectedInputTab = MutableStateFlow(0)
    private val _suggestedWords = MutableStateFlow<List<String>>(emptyList())
    private val _showHandwritingSheet = MutableStateFlow(false)


    val searchQuery: StateFlow<String> = _searchQuery
    val words: StateFlow<List<CCWord>> = _words
    val selectedInputTab: StateFlow<Int> = _selectedInputTab
    val suggestedWords: StateFlow<List<String>> = _suggestedWords.asStateFlow()
    val showHandwritingSheet: StateFlow<Boolean> = _showHandwritingSheet.asStateFlow()

    val focusRequester = FocusRequester()
    val localFocusManager = LocalFocusManager
    val localKeyboardController = LocalSoftwareKeyboardController

    init {
        viewModelScope.launch(Dispatchers.Main) {
            focusRequester.requestFocus()
            _searchQuery.collectLatest {
                search(it)
            }
        }
    }

    fun updateSearchQuery(newValue: String) {
        _searchQuery.value = newValue
    }

    fun updateInputTab(index: Int) {
        _selectedInputTab.value = index
    }

    fun setShowHandwritingSheet(boolean: Boolean){
        _showHandwritingSheet.value = boolean
    }

    fun processHandwritingStrokes(points: List<Offset>) {
        // This is where you would implement or call your handwriting recognition logic
        // For now, we'll just set sample suggestions

        // Example of setting suggestions:
        _suggestedWords.value = listOf("你", "好", "中国", "学习", "汉字")

        // In a real implementation, you would:
        // 1. Convert the points to a format your recognition API expects
        // 2. Call the recognition API
        // 3. Process the results
        // 4. Update the suggestedWords state with the recognition results
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
}

package com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels

import android.util.Log
import androidx.compose.ui.focus.FocusRequester
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordSearchViewModel(private val repository: CCWordRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    private val _words = MutableStateFlow<List<CCWord>>(emptyList())
    val words: StateFlow<List<CCWord>> = _words
    private val _selectedInputTab = MutableStateFlow(0)
    val selectedInputTab: StateFlow<Int> = _selectedInputTab

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

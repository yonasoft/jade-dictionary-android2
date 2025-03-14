package com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels

import android.util.Log
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordListRepository
import com.yonasoft.jadedictionary.features.word_lists.presentation.state.UIState
import com.yonasoft.jadedictionary.features.word_lists.presentation.state.WordListsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordListsViewModel(private val repository: CCWordListRepository) : ViewModel() {

    // Bundled state groups
    private val _wordListsState = MutableStateFlow(WordListsState())
    val wordListsState: StateFlow<WordListsState> = _wordListsState.asStateFlow()

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    // Backward compatibility properties
    val searchQuery: StateFlow<String>
        get() = MutableStateFlow(_wordListsState.value.searchQuery)

    val selectedTab: StateFlow<Int>
        get() = MutableStateFlow(_uiState.value.selectedTab)

    val myWordLists: StateFlow<List<CCWordList>>
        get() = MutableStateFlow(_wordListsState.value.myWordLists)

    // Utility objects
    val focusRequester = FocusRequester()
    val localFocusManager = LocalFocusManager
    val localKeyboardController = LocalSoftwareKeyboardController

    init {
        viewModelScope.launch {
            getMyWordLists()
        }
        viewModelScope.launch {
            _wordListsState.collectLatest {
                searchMyWordLists(it.searchQuery)
            }
        }
    }

    fun updateSearchQuery(newValue: String) {
        _wordListsState.update { it.copy(searchQuery = newValue) }
    }

    fun updateInputTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

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

            val insertedId = repository.insertWordList(newWordList)

            if (insertedId > 0) {
                searchMyWordLists()
            } else {
                // Handle error
                _uiState.update { it.copy(errorMessage = "Failed to create word list") }
            }
        }
    }

    private suspend fun getMyWordLists() {
        withContext(Dispatchers.IO) {
            val lists = repository.getAllWordLists()
            Log.e("word lists", lists.toString())
            withContext(Dispatchers.Main) {
                _wordListsState.update { it.copy(myWordLists = lists) }
            }
        }
    }

    private suspend fun searchMyWordLists(query: String = _wordListsState.value.searchQuery) {
        withContext(Dispatchers.IO) {
            val results = repository.searchWordLists(query)
            withContext(Dispatchers.Main) {
                _wordListsState.update { it.copy(myWordLists = results) }
            }
        }
    }
}
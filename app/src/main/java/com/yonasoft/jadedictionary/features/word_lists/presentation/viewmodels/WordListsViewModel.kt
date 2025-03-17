package com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKVersion
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWordRepository
import com.yonasoft.jadedictionary.features.word_lists.data.hsk.HSKWordListGenerator
import com.yonasoft.jadedictionary.features.word_lists.domain.WordList
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

class WordListsViewModel(
    private val ccWordListRepository: CCWordListRepository,
    private val hskWordRepository: HSKWordRepository
) : ViewModel() {

    // Bundled state groups
    private val _wordListsState = MutableStateFlow(WordListsState())
    val wordListsState: StateFlow<WordListsState> = _wordListsState.asStateFlow()

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    // Backward compatibility properties
    val searchQuery: StateFlow<String>
        get() = MutableStateFlow(_wordListsState.value.searchQuery)

    init {
        viewModelScope.launch {
            getMyWordLists()
            loadHSKWordLists()
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

            val insertedId = ccWordListRepository.insertWordList(newWordList)

            if (insertedId > 0) {
                searchMyWordLists()
            } else {
                // Handle error
                _uiState.update { it.copy(errorMessage = "Failed to create word list") }
            }
        }
    }

    fun deleteWordList(wordList: WordList) {
        // Only custom word lists can be deleted
        if (!wordList.isEditable) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                (wordList as? CCWordList)?.let {
                    ccWordListRepository.deleteWordList(it)

                    // Refresh the list after deletion
                    searchMyWordLists()
                }
            } catch (e: Exception) {
                Log.e("WordListsViewModel", "Error deleting word list", e)
                _uiState.update { it.copy(errorMessage = "Failed to delete word list") }
            }
        }
    }

    private suspend fun getMyWordLists() {
        withContext(Dispatchers.IO) {
            val lists = ccWordListRepository.getAllWordLists()
            Log.d("WordListsViewModel", "Loaded ${lists.size} custom word lists")

            withContext(Dispatchers.Main) {
                _wordListsState.update { it.copy(myWordLists = lists) }
            }
        }
    }

    private suspend fun loadHSKWordLists() {
        _wordListsState.update { it.copy(isLoading = true) }

        withContext(Dispatchers.IO) {
            try {
                // Generate HSK word lists for both versions
                val hskOldLists = HSKWordListGenerator.generateHSKWordLists(
                    repository = hskWordRepository,
                    version = HSKVersion.OLD
                )

                val hskNewLists = HSKWordListGenerator.generateHSKWordLists(
                    repository = hskWordRepository,
                    version = HSKVersion.NEW
                )

                Log.d("WordListsViewModel", "Loaded ${hskOldLists.size} HSK 2.0 lists")
                Log.d("WordListsViewModel", "Loaded ${hskNewLists.size} HSK 3.0 lists")

                withContext(Dispatchers.Main) {
                    _wordListsState.update { it.copy(
                        hskOldWordLists = hskOldLists,
                        hskNewWordLists = hskNewLists,
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                Log.e("WordListsViewModel", "Error loading HSK word lists", e)
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(errorMessage = "Failed to load HSK lists: ${e.message}") }
                    _wordListsState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private suspend fun searchMyWordLists(query: String = _wordListsState.value.searchQuery) {
        withContext(Dispatchers.IO) {
            val results = ccWordListRepository.searchWordLists(query)

            // Also filter HSK lists if there's a query
            val filteredHSKOldLists = if (query.isBlank()) {
                _wordListsState.value.hskOldWordLists
            } else {
                _wordListsState.value.hskOldWordLists.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            }

            val filteredHSKNewLists = if (query.isBlank()) {
                _wordListsState.value.hskNewWordLists
            } else {
                _wordListsState.value.hskNewWordLists.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            }

            withContext(Dispatchers.Main) {
                _wordListsState.update {
                    it.copy(
                        myWordLists = results,
                        hskOldWordLists = filteredHSKOldLists,
                        hskNewWordLists = filteredHSKNewLists
                    )
                }
            }
        }
    }
}
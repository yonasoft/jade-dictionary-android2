package com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.core.words.data.cc.CCWord
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class WordDetailViewModel(
    private val repository: CCWordRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _wordId = MutableStateFlow<Long?>(null)
    private val _wordDetails = MutableStateFlow<CCWord?>(null)
    private val _characters = MutableStateFlow<List<CCWord>>(emptyList())
    private val _wordsOfWord = MutableStateFlow<List<CCWord>>(emptyList())
    private val _selectedTab = MutableStateFlow(0)

    val wordDetails: StateFlow<CCWord?> = _wordDetails.asStateFlow()
    val characters: StateFlow<List<CCWord>> = _characters.asStateFlow()
    val wordsOfWord: StateFlow<List<CCWord>> = _wordsOfWord.asStateFlow()
    val tabIndex: StateFlow<Int> = _selectedTab.asStateFlow()

    init {
        viewModelScope.launch {
            val id = savedStateHandle.get<Long>("wordId")
            _wordId.value = id

            _wordId
                .filterNotNull()
                .collect { wordId ->
                    fetchWordDetails(wordId)
                }
        }

        viewModelScope.launch {
            _wordDetails
                .filterNotNull()
                .collect { word ->
                    word.simplified?.let {
                        fetchCharacters(it)
                        fetchWordsOfWord(it)
                    }
                }
        }
    }

    fun updateSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    private suspend fun fetchWordDetails(wordId: Long) {
        runCatching {
            val wordDetails = repository.getWordById(wordId)
            _wordDetails.value = wordDetails
        }.onFailure { e ->
            Log.e("WordDetailViewModel", "Error fetching word details", e)
        }
    }

    private suspend fun fetchCharacters(characters: String) {
        runCatching {
            val fetchedCharacters = repository.getCharsFromWord(characters)
            _characters.value = fetchedCharacters
        }.onFailure { e ->
            Log.e("WordDetailViewModel", "Error fetching characters", e)
        }
    }

    private suspend fun fetchWordsOfWord(word: String) {
        runCatching {
            val fetchedWord = repository.getWordsFromWord(word)
            _wordsOfWord.value = fetchedWord
        }.onFailure { e ->
            Log.e("WordDetailViewModel", "Error fetching characters", e)
        }
    }
}
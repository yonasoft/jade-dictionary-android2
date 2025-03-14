package com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.core.words.data.sentences.Sentence
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWord
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordDetailViewModel(
    private val repository: CCWordRepository,
    private val wordListRepository: CCWordListRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _wordId = MutableStateFlow<Long?>(null)
    private val _wordDetails = MutableStateFlow<CCWord?>(null)
    private val _characters = MutableStateFlow<List<CCWord>>(emptyList())
    private val _wordsOfWord = MutableStateFlow<List<CCWord>>(emptyList())
    private val _sentences = MutableStateFlow<List<Sentence>>(emptyList())
    private val _selectedTab = MutableStateFlow(0)
    private val _isSpeaking = MutableStateFlow(false)
    private val _wordLists = MutableStateFlow<List<CCWordList>>(emptyList())

    val wordDetails: StateFlow<CCWord?> = _wordDetails.asStateFlow()
    val characters: StateFlow<List<CCWord>> = _characters.asStateFlow()
    val wordsOfWord: StateFlow<List<CCWord>> = _wordsOfWord.asStateFlow()
    val sentences: StateFlow<List<Sentence>> = _sentences.asStateFlow()
    val tabIndex: StateFlow<Int> = _selectedTab.asStateFlow()
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()
    val wordLists: StateFlow<List<CCWordList>> = _wordLists.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val id = savedStateHandle.get<Long>("wordId")
            _wordId.value = id

            _wordId
                .filterNotNull()
                .collect { wordId ->
                    fetchWordDetails(wordId)
                }
        }

        viewModelScope.launch(Dispatchers.Unconfined) {
            _wordDetails
                .filterNotNull()
                .collectLatest { word ->
                    word.simplified?.let {
                        fetchCharacters(it)
                        fetchWordsOfWord(it)
                        fetchSentencesOfWord(it)
                    }
                }
        }

        // Load word lists
        loadWordLists()
    }

    fun updateSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun setIsSpeaking(boolean: Boolean) {
        _isSpeaking.value = boolean
    }

    /**
     * Loads all word lists from the repository
     */
    private fun loadWordLists() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lists = wordListRepository.getAllWordLists()
                withContext(Dispatchers.Main) {
                    _wordLists.value = lists
                }
            } catch (e: Exception) {
                Log.e("WordDetailViewModel", "Error loading word lists", e)
            }
        }
    }

    /**
     * Creates a new word list
     */
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

                val insertedId = wordListRepository.insertWordList(newWordList)
                Log.d("WordDetailViewModel", "Created new word list with ID: $insertedId")

                // Reload word lists to include the new one
                loadWordLists()
            } catch (e: Exception) {
                Log.e("WordDetailViewModel", "Error creating word list", e)
            }
        }
    }

    /**
     * Adds the current word to a selected word list
     */
    fun addWordToList(wordList: CCWordList) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentWord = _wordDetails.value ?: return@launch
                val wordId = currentWord.id ?: return@launch

                // Check if word is already in the list
                val existingIds = wordList.wordIds.toMutableList()

                // Only add if not already in list
                if (!existingIds.contains(wordId)) {
                    existingIds.add(wordId)

                    // Update the word list with new wordIds and count
                    val updatedList = wordList.copy(
                        wordIds = existingIds,
                        numberOfWords = existingIds.size.toLong(),
                        updatedAt = System.currentTimeMillis()
                    )

                    // Update in database
                    wordListRepository.updateWordList(updatedList)

                    // Refresh word lists
                    loadWordLists()
                }
            } catch (e: Exception) {
                Log.e("WordDetailViewModel", "Error adding word to list", e)
            }
        }
    }

    private suspend fun fetchWordDetails(wordId: Long) {
        withContext(Dispatchers.IO) {
            try {
                val wordDetails = repository.getWordById(wordId)
                withContext(Dispatchers.Main) {
                    _wordDetails.value = wordDetails
                }
            } catch (e: Exception) {
                Log.e("WordDetailViewModel", "Error fetching word details", e)
            }
        }
    }

    private suspend fun fetchCharacters(characters: String) {
        withContext(Dispatchers.IO) {
            try {
                val fetchedCharacters = repository.getCharsFromWord(characters)
                withContext(Dispatchers.Main) {
                    _characters.value = fetchedCharacters
                }
            } catch (e: Exception) {
                Log.e("WordDetailViewModel", "Error fetching characters", e)
            }
        }
    }

    private suspend fun fetchWordsOfWord(word: String) {
        withContext(Dispatchers.IO) {
            try {
                val fetchedWord = repository.getWordsFromWord(word)
                withContext(Dispatchers.Main) {
                    _wordsOfWord.value = fetchedWord
                }
            } catch (e: Exception) {
                Log.e("WordDetailViewModel", "Error fetching words of word", e)
            }
        }
    }

    private suspend fun fetchSentencesOfWord(word: String) {
        withContext(Dispatchers.IO) {
            try {
                val fetchedSentences = repository.getSentencesFromWord(word)
                Log.i("WordDetailViewModel", fetchedSentences.toString())
                withContext(Dispatchers.Main) {
                    _sentences.value = fetchedSentences
                }
            } catch (e: Exception) {
                Log.e("WordDetailViewModel", "Error fetching words of word", e)
            }
        }
    }
}
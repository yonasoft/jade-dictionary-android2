package com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWord
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordListRepository
import com.yonasoft.jadedictionary.features.word_lists.presentation.state.WordListDetailState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordListDetailViewModel(
    private val wordListRepository: CCWordListRepository,
    private val wordRepository: CCWordRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val wordListId: Long = checkNotNull(savedStateHandle["wordListId"])

    private val _uiState = MutableStateFlow(WordListDetailState())
    val uiState: StateFlow<WordListDetailState> = _uiState.asStateFlow()

    // Track the current undo job to cancel it if needed
    private var undoJob: Job? = null

    // How long the undo option remains available (in milliseconds)
    private val UNDO_TIMEOUT = 5000L

    init {
        loadWordList()
    }

    private fun loadWordList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                withContext(Dispatchers.IO) {
                    val wordList = wordListRepository.getWordListById(wordListId)

                    if (wordList != null) {
                        val wordIds = wordList.wordIds
                        val words = if (wordIds.isNotEmpty()) {
                            wordRepository.getWordByIds(wordIds)
                        } else {
                            emptyList()
                        }

                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    wordList = wordList,
                                    words = words,
                                    filteredWords = words,
                                    editTitle = wordList.title,
                                    editDescription = wordList.description,
                                    isLoading = false
                                )
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    errorMessage = "Word list not found",
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("WordListDetailVM", "Error loading word list", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to load word list: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun searchWords(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        val currentWords = _uiState.value.words
        val filteredWords = if (query.isBlank()) {
            currentWords
        } else {
            currentWords.filter { word ->
                word.displayText.contains(query, ignoreCase = true) ||
                        word.pinyin?.contains(query, ignoreCase = true) == true ||
                        word.definition?.contains(query, ignoreCase = true) == true
            }
        }

        _uiState.update { it.copy(filteredWords = filteredWords) }
    }

    fun startEditing() {
        _uiState.update {
            it.copy(
                isEditing = true,
                editTitle = it.wordList?.title ?: "",
                editDescription = it.wordList?.description ?: ""
            )
        }
    }

    fun cancelEditing() {
        _uiState.update { it.copy(isEditing = false) }
    }

    fun updateEditTitle(title: String) {
        _uiState.update { it.copy(editTitle = title) }
    }

    fun updateEditDescription(description: String) {
        _uiState.update { it.copy(editDescription = description) }
    }

    fun saveEdits() {
        viewModelScope.launch {
            try {
                val currentWordList = _uiState.value.wordList ?: return@launch
                val updatedWordList = currentWordList.copy(
                    title = _uiState.value.editTitle,
                    description = _uiState.value.editDescription,
                    updatedAt = System.currentTimeMillis()
                )

                withContext(Dispatchers.IO) {
                    wordListRepository.updateWordList(updatedWordList)

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                wordList = updatedWordList,
                                isEditing = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("WordListDetailVM", "Error updating word list", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to update word list: ${e.message}"
                    )
                }
            }
        }
    }

    fun removeWord(word: CCWord) {
        // Cancel any existing undo job
        undoJob?.cancel()

        viewModelScope.launch {
            try {
                val currentWordList = _uiState.value.wordList ?: return@launch
                val updatedWordIds = currentWordList.wordIds.toMutableList()

                // Remove the word ID
                updatedWordIds.remove(word.id)

                val updatedWordList = currentWordList.copy(
                    wordIds = updatedWordIds,
                    numberOfWords = updatedWordIds.size.toLong(),
                    updatedAt = System.currentTimeMillis()
                )

                withContext(Dispatchers.IO) {
                    wordListRepository.updateWordList(updatedWordList)

                    val updatedWords = _uiState.value.words.toMutableList()
                    updatedWords.remove(word)

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                wordList = updatedWordList,
                                words = updatedWords,
                                filteredWords = if (it.searchQuery.isBlank()) updatedWords else
                                    updatedWords.filter { filterWord ->
                                        filterWord.displayText.contains(it.searchQuery, ignoreCase = true) ||
                                                filterWord.pinyin?.contains(it.searchQuery, ignoreCase = true) == true ||
                                                filterWord.definition?.contains(it.searchQuery, ignoreCase = true) == true
                                    },
                                lastRemovedWord = word,
                                isUndoAvailable = true
                            )
                        }
                    }
                }

                // Start undo timeout
                undoJob = startUndoTimeout()

            } catch (e: Exception) {
                Log.e("WordListDetailVM", "Error removing word", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to remove word: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Undoes the last word removal
     */
    fun undoWordRemoval() {
        undoJob?.cancel()

        viewModelScope.launch {
            try {
                val currentWordList = _uiState.value.wordList ?: return@launch
                val lastRemovedWord = _uiState.value.lastRemovedWord ?: return@launch

                // Add the word ID back to the list
                val updatedWordIds = currentWordList.wordIds.toMutableList()
                lastRemovedWord.id?.let { updatedWordIds.add(it) }

                val updatedWordList = currentWordList.copy(
                    wordIds = updatedWordIds,
                    numberOfWords = updatedWordIds.size.toLong(),
                    updatedAt = System.currentTimeMillis()
                )

                withContext(Dispatchers.IO) {
                    wordListRepository.updateWordList(updatedWordList)

                    // Add the word back to the lists
                    val updatedWords = _uiState.value.words.toMutableList()
                    updatedWords.add(lastRemovedWord)

                    // Re-filter if needed
                    val updatedFilteredWords = if (_uiState.value.searchQuery.isBlank()) {
                        updatedWords
                    } else {
                        updatedWords.filter { word ->
                            word.displayText.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                                    word.pinyin?.contains(_uiState.value.searchQuery, ignoreCase = true) == true ||
                                    word.definition?.contains(_uiState.value.searchQuery, ignoreCase = true) == true
                        }
                    }

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                wordList = updatedWordList,
                                words = updatedWords,
                                filteredWords = updatedFilteredWords,
                                lastRemovedWord = null,
                                isUndoAvailable = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("WordListDetailVM", "Error undoing word removal", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to undo word removal: ${e.message}",
                        isUndoAvailable = false
                    )
                }
            }
        }
    }

    /**
     * Starts a timeout after which the undo option will no longer be available
     */
    private fun startUndoTimeout(): Job {
        return viewModelScope.launch {
            try {
                delay(UNDO_TIMEOUT)
                // After timeout, remove the undo option
                _uiState.update {
                    it.copy(
                        isUndoAvailable = false,
                        lastRemovedWord = null
                    )
                }
            } catch (e: Exception) {
                // Job was likely canceled, no action needed
            }
        }
    }
}
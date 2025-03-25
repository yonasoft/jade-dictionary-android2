package com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.features.word.domain.Word
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWordRepository
import com.yonasoft.jadedictionary.features.word_lists.data.hsk.HSKWordListGenerator
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordListRepository
import com.yonasoft.jadedictionary.features.word_lists.domain.hsk.HSKWordList
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
    private val ccWordListRepository: CCWordListRepository,
    private val ccWordRepository: CCWordRepository,
    private val hskWordRepository: HSKWordRepository,
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
                // Check if this is an HSK list based on ID range
                if (wordListId >= 1000000) {
                    loadHSKWordList(wordListId)
                } else {
                    loadCustomWordList(wordListId)
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

    private suspend fun loadCustomWordList(id: Long) {
        withContext(Dispatchers.IO) {
            val wordList = ccWordListRepository.getWordListById(id)

            if (wordList != null) {
                val wordIds = wordList.wordIds
                val words = if (wordIds.isNotEmpty()) {
                    ccWordRepository.getWordByIds(wordIds)
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
                            isLoading = false,
                            isHSKList = false
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
    }

    private suspend fun loadHSKWordList(id: Long) {
        withContext(Dispatchers.IO) {
            try {
                // Extract version and level from ID
                val versionCode = ((id - 1000000) / 1000).toInt()
                val levelValue = ((id - 1000000) % 1000).toInt()

                val version = if (versionCode == 1) {
                    com.yonasoft.jadedictionary.features.word.domain.hsk.HSKVersion.OLD
                } else {
                    com.yonasoft.jadedictionary.features.word.domain.hsk.HSKVersion.NEW
                }

                val level =
                    com.yonasoft.jadedictionary.features.word.domain.hsk.HSKLevel.fromInt(levelValue)

                if (level == null) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                errorMessage = "Invalid HSK level",
                                isLoading = false
                            )
                        }
                    }
                    return@withContext
                }

                // Create HSK word list
                val hskWordList = HSKWordList(
                    id = id,
                    title = "${if (version == com.yonasoft.jadedictionary.features.word.domain.hsk.HSKVersion.OLD) "HSK 2.0" else "HSK 3.0"} Level ${level.value}",
                    description = "Official ${if (version == com.yonasoft.jadedictionary.features.word.domain.hsk.HSKVersion.OLD) "HSK 2.0" else "HSK 3.0"} vocabulary list for Level ${level.value}",
                    version = version,
                    level = level,
                    wordCount = 0 // Will be updated with actual count
                )

                // Get HSK words for this list
                val words = HSKWordListGenerator.getHSKWordsForList(hskWordRepository, hskWordList)

                // Create updated list with proper word count
                val updatedHskWordList = hskWordList.copy(wordCount = words.size)

                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            wordList = updatedHskWordList,
                            words = words,
                            filteredWords = words,
                            isLoading = false,
                            isHSKList = true
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e("WordListDetailVM", "Error loading HSK list", e)
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Failed to load HSK list: ${e.message}",
                            isLoading = false
                        )
                    }
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
                when (word) {
                    is CCWord -> {
                        word.displayText.contains(query, ignoreCase = true) ||
                                word.pinyin?.contains(query, ignoreCase = true) == true ||
                                word.definition?.contains(query, ignoreCase = true) == true
                    }

                    is com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord -> {
                        word.displayText.contains(query, ignoreCase = true) ||
                                word.pinyin?.contains(query, ignoreCase = true) == true ||
                                word.definitions.any { it.contains(query, ignoreCase = true) }
                    }

                    else -> false
                }
            }
        }

        _uiState.update { it.copy(filteredWords = filteredWords) }
    }

    fun startEditing() {
        // Only allow editing for custom word lists
        if (_uiState.value.isHSKList) return

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
        // Only allow editing for custom word lists
        if (_uiState.value.isHSKList) return

        viewModelScope.launch {
            try {
                val currentWordList = _uiState.value.wordList as? CCWordList ?: return@launch
                val updatedWordList = currentWordList.copy(
                    title = _uiState.value.editTitle,
                    description = _uiState.value.editDescription,
                    updatedAt = System.currentTimeMillis()
                )

                withContext(Dispatchers.IO) {
                    ccWordListRepository.updateWordList(updatedWordList)

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

    fun removeWord(word: Word) {
        // Only allow removing words from custom word lists
        if (_uiState.value.isHSKList) return

        // Cancel any existing undo job
        undoJob?.cancel()

        viewModelScope.launch {
            try {
                val currentWordList = _uiState.value.wordList as? CCWordList ?: return@launch
                word as? CCWord ?: return@launch

                val updatedWordIds = currentWordList.wordIds.toMutableList()

                // Remove the word ID
                updatedWordIds.remove(word.id)

                val updatedWordList = currentWordList.copy(
                    wordIds = updatedWordIds,
                    numberOfWords = updatedWordIds.size.toLong(),
                    updatedAt = System.currentTimeMillis()
                )

                withContext(Dispatchers.IO) {
                    ccWordListRepository.updateWordList(updatedWordList)

                    val updatedWords = _uiState.value.words.toMutableList()
                    updatedWords.remove(word)

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                wordList = updatedWordList,
                                words = updatedWords,
                                filteredWords = if (it.searchQuery.isBlank()) updatedWords else
                                    updatedWords.filter { filterWord ->
                                        when (filterWord) {
                                            is CCWord -> {
                                                filterWord.displayText.contains(
                                                    it.searchQuery,
                                                    ignoreCase = true
                                                ) ||
                                                        filterWord.pinyin?.contains(
                                                            it.searchQuery,
                                                            ignoreCase = true
                                                        ) == true ||
                                                        filterWord.definition?.contains(
                                                            it.searchQuery,
                                                            ignoreCase = true
                                                        ) == true
                                            }

                                            is com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord -> {
                                                filterWord.displayText.contains(
                                                    it.searchQuery,
                                                    ignoreCase = true
                                                ) ||
                                                        filterWord.pinyin?.contains(
                                                            it.searchQuery,
                                                            ignoreCase = true
                                                        ) == true ||
                                                        filterWord.definitions.any { def ->
                                                            def.contains(
                                                                it.searchQuery,
                                                                ignoreCase = true
                                                            )
                                                        }
                                            }

                                            else -> false
                                        }
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
        // Only allow operations on custom word lists
        if (_uiState.value.isHSKList) return

        undoJob?.cancel()

        viewModelScope.launch {
            try {
                val currentWordList = _uiState.value.wordList as? CCWordList ?: return@launch
                val lastRemovedWord = _uiState.value.lastRemovedWord as? CCWord ?: return@launch

                // Add the word ID back to the list
                val updatedWordIds = currentWordList.wordIds.toMutableList()
                lastRemovedWord.id?.let { updatedWordIds.add(it) }

                val updatedWordList = currentWordList.copy(
                    wordIds = updatedWordIds,
                    numberOfWords = updatedWordIds.size.toLong(),
                    updatedAt = System.currentTimeMillis()
                )

                withContext(Dispatchers.IO) {
                    ccWordListRepository.updateWordList(updatedWordList)

                    // Add the word back to the lists
                    val updatedWords = _uiState.value.words.toMutableList()
                    updatedWords.add(lastRemovedWord)

                    // Re-filter if needed
                    val updatedFilteredWords = if (_uiState.value.searchQuery.isBlank()) {
                        updatedWords
                    } else {
                        updatedWords.filter { word ->
                            when (word) {
                                is CCWord -> {
                                    word.displayText.contains(
                                        _uiState.value.searchQuery,
                                        ignoreCase = true
                                    ) ||
                                            word.pinyin?.contains(
                                                _uiState.value.searchQuery,
                                                ignoreCase = true
                                            ) == true ||
                                            word.definition?.contains(
                                                _uiState.value.searchQuery,
                                                ignoreCase = true
                                            ) == true
                                }

                                is com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord -> {
                                    word.displayText.contains(
                                        _uiState.value.searchQuery,
                                        ignoreCase = true
                                    ) ||
                                            word.pinyin?.contains(
                                                _uiState.value.searchQuery,
                                                ignoreCase = true
                                            ) == true ||
                                            word.definitions.any {
                                                it.contains(
                                                    _uiState.value.searchQuery,
                                                    ignoreCase = true
                                                )
                                            }
                                }

                                else -> false
                            }
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

    fun resetQuery() {
        _uiState.update {
            it.copy(
                searchQuery = ""
            )
        }
    }
}
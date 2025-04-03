package com.yonasoft.jadedictionary.features.practice.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.features.practice.domain.models.shared.PracticeType
import com.yonasoft.jadedictionary.features.practice.presentation.state.CCPracticeSetupState
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CCPracticeSetupViewModel(
    private val ccWordRepository: CCWordRepository,
    private val ccWordListRepository: CCWordListRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Extract practice type from navigation arguments
    private val practiceTypeArg: String? = savedStateHandle["practiceType"]

    // UI state
    private val _uiState = MutableStateFlow(CCPracticeSetupState())
    val uiState: StateFlow<CCPracticeSetupState> = _uiState.asStateFlow()

    // Cached word lists
    private var wordLists: List<CCWordList> = emptyList()

    // Undo timeout job
    private var undoJob: Job? = null

    // How long the undo option remains available
    private val UNDO_TIMEOUT = 5000L

    init {
        // Set practice type from navigation
        val type = when (practiceTypeArg) {
            "flash_cards" -> PracticeType.FLASH_CARDS
            "multiple_choice" -> PracticeType.MULTIPLE_CHOICE
            "listening" -> PracticeType.LISTENING
            else -> null
        }

        _uiState.update { it.copy(practiceType = type) }

        // Load word lists in background
        loadWordLists()
    }

    /**
     * Load all word lists for the modal
     */
    private fun loadWordLists() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                wordLists = ccWordListRepository.getAllWordLists()
            } catch (e: Exception) {
                Log.e("CCPracticeSetupVM", "Error loading word lists", e)
                _uiState.update { it.copy(errorMessage = "Failed to load word lists: ${e.message}") }
            }
        }
    }

    /**
     * Get all word lists
     */
    fun getWordLists(): List<CCWordList> = wordLists

    /**
     * Search for words based on query
     */
    fun searchWords(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList()) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val results = ccWordRepository.searchWords(query)
                _uiState.update {
                    it.copy(
                        searchResults = results,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("CCPracticeSetupVM", "Error searching words", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to search words: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Add a word to the practice session
     */
    fun addWord(word: CCWord) {
        val currentWords = _uiState.value.selectedWords.toMutableList()

        // Check if word is already in the list
        if (currentWords.any { it.id == word.id }) {
            _uiState.update { it.copy(errorMessage = "Word '${word.displayText}' is already in your practice list") }
            return
        }

        currentWords.add(word)
        _uiState.update { it.copy(selectedWords = currentWords) }
    }

    /**
     * Remove a word from the practice session
     */
    fun removeWord(word: CCWord) {
        // Cancel any existing undo job
        undoJob?.cancel()

        val currentWords = _uiState.value.selectedWords.toMutableList()
        if (currentWords.remove(word)) {
            _uiState.update {
                it.copy(
                    selectedWords = currentWords,
                    lastRemovedWord = word,
                    isUndoAvailable = true
                )
            }

            // Start undo timeout
            undoJob = startUndoTimeout()
        }
    }

    /**
     * Undo the last word removal
     */
    fun undoWordRemoval() {
        undoJob?.cancel()

        val lastRemovedWord = _uiState.value.lastRemovedWord ?: return
        val currentWords = _uiState.value.selectedWords.toMutableList()
        currentWords.add(lastRemovedWord)

        _uiState.update {
            it.copy(
                selectedWords = currentWords,
                lastRemovedWord = null,
                isUndoAvailable = false
            )
        }
    }

    /**
     * Add words from a word list
     */
    fun addWordsFromList(wordList: CCWordList) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Get words from the list
                val wordIds = wordList.wordIds
                if (wordIds.isEmpty()) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "The selected word list is empty."
                    ) }
                    return@launch
                }

                val words = ccWordRepository.getWordByIds(wordIds)

                // Add only words that aren't already in the selected list
                val currentIds = _uiState.value.selectedWords.mapNotNull { it.id }
                val newWords = words.filter { it.id !in currentIds }

                if (newWords.isEmpty()) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "All words from this list are already added."
                    ) }
                    return@launch
                }

                val updatedWords = _uiState.value.selectedWords + newWords

                _uiState.update { it.copy(
                    selectedWords = updatedWords,
                    isLoading = false,
                    isWordListModalOpen = false
                ) }
            } catch (e: Exception) {
                Log.e("CCPracticeSetupVM", "Error adding words from list", e)
                _uiState.update { it.copy(
                    errorMessage = "Failed to add words from list: ${e.message}",
                    isLoading = false
                ) }
            }
        }
    }

    /**
     * Open search modal
     */
    fun openSearchModal() {
        _uiState.update {
            it.copy(
                isSearchModalOpen = true,
                searchQuery = "",
                searchResults = emptyList()
            )
        }
    }

    /**
     * Close search modal
     */
    fun closeSearchModal() {
        _uiState.update { it.copy(isSearchModalOpen = false) }
    }

    /**
     * Open word list modal
     */
    fun openWordListModal() {
        _uiState.update { it.copy(isWordListModalOpen = true) }
    }

    /**
     * Close word list modal
     */
    fun closeWordListModal() {
        _uiState.update { it.copy(isWordListModalOpen = false) }
    }

    /**
     * Clear error message
     */
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
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
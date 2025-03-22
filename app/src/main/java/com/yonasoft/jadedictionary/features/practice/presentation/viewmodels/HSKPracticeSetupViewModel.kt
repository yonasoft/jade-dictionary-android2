package com.yonasoft.jadedictionary.features.practice.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.features.practice.domain.models.shared.PracticeType
import com.yonasoft.jadedictionary.features.practice.presentation.state.HSKPracticeSetupState
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKLevel
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKVersion
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HSKPracticeSetupViewModel(
    private val hskWordRepository: HSKWordRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Extract practice type from navigation arguments
    private val practiceTypeArg: String? = savedStateHandle["practiceType"]

    // UI state
    private val _uiState = MutableStateFlow(HSKPracticeSetupState())
    val uiState: StateFlow<HSKPracticeSetupState> = _uiState.asStateFlow()

    // Full list of available words for the selected HSK levels
    private var availableWords: List<HSKWord> = emptyList()

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
    }

    /**
     * Set HSK version
     */
    fun setHSKVersion(version: HSKVersion) {
        _uiState.update { it.copy(selectedHSKVersion = version) }
    }

    /**
     * Select HSK levels to practice
     */
    fun selectHSKLevels(levels: List<Int>, version: HSKVersion? = null) {
        if (levels.isEmpty()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Please select at least one HSK level"
                )
            }
            return
        }

        // Use provided version or current selected version
        val hskVersion = version ?: uiState.value.selectedHSKVersion

        // Map integer levels to HSKLevel enum values for repository queries
        val hskLevels = levels.mapNotNull { level -> HSKLevel.fromInt(level) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Load words for selected levels
                availableWords = hskWordRepository.getWordsByLevels(hskVersion, hskLevels)

                if (availableWords.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            errorMessage = "No words found for selected HSK levels",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                // Default to using all words if less than 20, otherwise use 20
                val defaultWordCount = if (availableWords.size <= 20) {
                    availableWords.size
                } else {
                    20
                }.coerceAtLeast(4) // Ensure at least 4 words

                _uiState.update {
                    it.copy(
                        selectedHSKVersion = hskVersion,
                        selectedHSKLevels = levels.sorted(),
                        availableWordCount = availableWords.size,
                        randomWordCount = defaultWordCount,
                        isLoading = false,
                        isHSKLevelModalOpen = false
                    )
                }

                // Generate random words based on the selected levels
                generateRandomWords()

            } catch (e: Exception) {
                Log.e("HSKPracticeSetupVM", "Error loading HSK words", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to load HSK words: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Set the number of random words to use
     */
    fun setRandomWordCount(count: Int) {
        val maxCount = uiState.value.availableWordCount
        val finalCount = count.coerceIn(4, maxCount)

        _uiState.update {
            it.copy(
                randomWordCount = finalCount
            )
        }
    }

    /**
     * Generate a random selection of words from the available words
     */
    fun generateRandomWords() {
        val count = uiState.value.randomWordCount
        val availableWordCount = availableWords.size

        if (count > availableWordCount) {
            _uiState.update {
                it.copy(
                    errorMessage = "Cannot select more words than available ($availableWordCount)"
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Randomly select words
                val randomizedWords = availableWords.shuffled().take(count)

                _uiState.update {
                    it.copy(
                        selectedWords = randomizedWords,
                        isLoading = false,
                        isCountSelectorOpen = false
                    )
                }
            } catch (e: Exception) {
                Log.e("HSKPracticeSetupVM", "Error generating random words", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to generate random words: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Remove a word from the practice session
     */
    fun removeWord(word: HSKWord) {
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
     * Open HSK level selection modal
     */
    fun openHSKLevelModal() {
        _uiState.update { it.copy(isHSKLevelModalOpen = true) }
    }

    /**
     * Close HSK level selection modal
     */
    fun closeHSKLevelModal() {
        _uiState.update { it.copy(isHSKLevelModalOpen = false) }
    }

    /**
     * Open count selector modal
     */
    fun openCountSelectorModal() {
        _uiState.update { it.copy(isCountSelectorOpen = true) }
    }

    /**
     * Close count selector modal
     */
    fun closeCountSelectorModal() {
        _uiState.update { it.copy(isCountSelectorOpen = false) }
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
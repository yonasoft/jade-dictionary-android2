package com.yonasoft.jadedictionary.features.practice.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.features.practice.presentation.state.CardDisplayMode
import com.yonasoft.jadedictionary.features.practice.presentation.state.FlashCardState
import com.yonasoft.jadedictionary.features.practice.presentation.state.WordDifficulty
import com.yonasoft.jadedictionary.features.practice.presentation.state.WordResult
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashCardPracticeViewModel(
    private val ccWordRepository: CCWordRepository,
    private val hskWordRepository: HSKWordRepository,
    private val savedStateHandle: androidx.lifecycle.SavedStateHandle,
) : ViewModel() {

    // State
    private val _uiState = MutableStateFlow(FlashCardState())
    val uiState: StateFlow<FlashCardState> = _uiState.asStateFlow()

    // Handle parameters from navigation
    private val wordSource: String = savedStateHandle.get<String>("wordSource") ?: "CC"
    private val wordIdsString: String = savedStateHandle.get<String>("wordIds") ?: ""

    private val wordIds: List<Long> = if (wordIdsString.isNotEmpty()) {
        wordIdsString.split(",").map { it.toLong() }
    } else {
        emptyList()
    }

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch(Dispatchers.IO) {
            // Start with loading state
            _uiState.update { it.copy(isLoading = true) }

            val words = when (wordSource) {
                "CC" -> ccWordRepository.getWordByIds(wordIds.shuffled())
                "HSK" -> wordIds.shuffled().mapNotNull { hskWordRepository.getWordById(it) }
                else -> emptyList()
            }

            // Update state with loaded words
            _uiState.update {
                it.copy(
                    isLoading = false,
                    words = words,
                    totalWords = words.size,
                    // Initialize with a random display mode for the first card
                    currentCardMode = getRandomDisplayMode()
                )
            }
        }
    }

    // Get a random display mode for front of card
    private fun getRandomDisplayMode(): CardDisplayMode {
        return CardDisplayMode.entries.toTypedArray().random()
    }

    // Track user's progress through the card stack
    fun markCurrentWord(difficulty: WordDifficulty) {
        val currentState = _uiState.value
        val currentWord = currentState.getCurrentWord()

        if (currentWord != null) {
            // Create result for this word
            val result = WordResult(currentWord, difficulty)

            // Update the appropriate results list
            val easyWords = if (difficulty == WordDifficulty.EASY)
                currentState.easyWords + result else currentState.easyWords

            val mediumWords = if (difficulty == WordDifficulty.MEDIUM)
                currentState.mediumWords + result else currentState.mediumWords

            val hardWords = if (difficulty == WordDifficulty.HARD)
                currentState.hardWords + result else currentState.hardWords

            // Move to next card
            _uiState.update {
                it.copy(
                    currentWordIndex = it.currentWordIndex + 1,
                    easyWords = easyWords,
                    mediumWords = mediumWords,
                    hardWords = hardWords,
                    // Set a new random display mode for the next card
                    currentCardMode = getRandomDisplayMode(),
                    // Reset card flip state for the new card
                    isCardFlipped = false
                )
            }

            // Check if we've reached the end
            if (currentState.currentWordIndex + 1 >= currentState.words.size) {
                _uiState.update { it.copy(isPracticeComplete = true) }
            }
        }
    }

    fun flipCard() {
        _uiState.update { it.copy(isCardFlipped = !it.isCardFlipped) }
    }

    fun setResultsTabIndex(index: Int) {
        _uiState.update { it.copy(resultsTabIndex = index) }
    }
}
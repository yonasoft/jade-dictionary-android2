package com.yonasoft.jadedictionary.features.practice.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.features.practice.presentation.state.MultipleChoiceQuestion
import com.yonasoft.jadedictionary.features.practice.presentation.state.MultipleChoiceState
import com.yonasoft.jadedictionary.features.practice.presentation.state.QuestionMode
import com.yonasoft.jadedictionary.features.practice.presentation.state.QuestionResult
import com.yonasoft.jadedictionary.features.word.domain.Word
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MultipleChoicePracticeViewModel(
    private val ccWordRepository: CCWordRepository,
    private val hskWordRepository: HSKWordRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // State
    private val _uiState = MutableStateFlow(MultipleChoiceState())
    val uiState: StateFlow<MultipleChoiceState> = _uiState.asStateFlow()

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
            _uiState.update { it.copy(isLoading = true) }

            try {
                val words = when (wordSource) {
                    "CC" -> ccWordRepository.getWordByIds(wordIds.shuffled())
                    "HSK" -> wordIds.shuffled().mapNotNull { hskWordRepository.getWordById(it) }
                    else -> emptyList()
                }

                // Generate questions from the words
                val allQuestions = generateQuestions(words)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        words = words,
                        questions = allQuestions
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error loading words: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Generate a list of multiple choice questions from the given words
     */
    private fun generateQuestions(words: List<Word>): List<MultipleChoiceQuestion> {
        if (words.isEmpty()) return emptyList()

        val questions = mutableListOf<MultipleChoiceQuestion>()
        val wordPool = words.toMutableList()
        val optionsPerQuestion = 4 // Number of options to show for each question

        // Create a question for each word with random mode
        for (word in words) {
            val mode = QuestionMode.entries.random()
            val distractorWords = getDistractorWords(wordPool, word, optionsPerQuestion - 1)

            // Generate options based on mode
            val options = mutableListOf<String>()
            var correctOption = ""

            when (mode) {
                QuestionMode.PINYIN_TO_CHARACTER -> {
                    correctOption = getDisplayText(word)
                    options.add(correctOption)
                    options.addAll(distractorWords.map { getDisplayText(it) })
                }
                QuestionMode.CHARACTER_TO_PINYIN -> {
                    correctOption = getDisplayPinyin(word)
                    options.add(correctOption)
                    options.addAll(distractorWords.map { getDisplayPinyin(it) })
                }
                QuestionMode.CHARACTER_TO_DEFINITION -> {
                    correctOption = getDisplayDefinition(word)
                    options.add(correctOption)
                    options.addAll(distractorWords.map { getDisplayDefinition(it) })
                }
                QuestionMode.DEFINITION_TO_CHARACTER -> {
                    correctOption = getDisplayText(word)
                    options.add(correctOption)
                    options.addAll(distractorWords.map { getDisplayText(it) })
                }
            }

            // Shuffle options
            options.shuffle()

            // Find index of correct option after shuffling
            val correctIndex = options.indexOf(correctOption)

            // Create and add question
            questions.add(
                MultipleChoiceQuestion(
                    word = word,
                    mode = mode,
                    options = options,
                    correctOptionIndex = correctIndex
                )
            )
        }

        return questions
    }

    /**
     * Get a list of distractor words for multiple choice options
     */
    private fun getDistractorWords(wordPool: List<Word>, currentWord: Word, count: Int): List<Word> {
        // Create a mutable copy of the word pool without the current word
        val availableWords = wordPool.filter { it != currentWord }.toMutableList()

        // Ensure we have enough words for options
        if (availableWords.size < count) {
            // If not enough words, just use what we have
            return availableWords
        }

        // Shuffle and take required number of distractor words
        availableWords.shuffle()
        return availableWords.take(count)
    }

    /**
     * User selects an option
     */
    fun selectOption(optionIndex: Int) {
        val currentState = _uiState.value
        val currentQuestion = currentState.getCurrentQuestion() ?: return

        _uiState.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                isAnswerRevealed = true
            )
        }
    }

    /**
     * Move to the next question or complete practice
     */
    fun moveToNextQuestion() {
        val currentState = _uiState.value
        val currentQuestion = currentState.getCurrentQuestion() ?: return

        // Record result for the current question
        val result = QuestionResult(
            question = currentQuestion,
            selectedOptionIndex = currentState.selectedOptionIndex,
            isCorrect = currentState.selectedOptionIndex == currentQuestion.correctOptionIndex
        )

        val updatedResults = currentState.results + result
        val nextIndex = currentState.currentQuestionIndex + 1
        val isComplete = nextIndex >= currentState.questions.size

        _uiState.update {
            it.copy(
                currentQuestionIndex = nextIndex,
                results = updatedResults,
                isPracticeComplete = isComplete,
                isAnswerRevealed = false,
                selectedOptionIndex = -1
            )
        }
    }

    /**
     * Retry missed questions
     */
    fun retryMissedQuestions() {
        val currentState = _uiState.value

        // Get all incorrectly answered questions
        val missedQuestions = currentState.results
            .filter { !it.isCorrect }
            .map { it.question }

        if (missedQuestions.isEmpty()) return

        _uiState.update {
            it.copy(
                questions = missedQuestions,
                currentQuestionIndex = 0,
                results = emptyList(),
                isPracticeComplete = false,
                isAnswerRevealed = false,
                selectedOptionIndex = -1
            )
        }
    }

    // Helper functions to get display text from different word types
    private fun getDisplayText(word: Word): String {
        return when (word) {
            is CCWord -> word.displayText
            is HSKWord -> word.displayText
            else -> ""
        }
    }

    private fun getDisplayPinyin(word: Word): String {
        return when (word) {
            is CCWord -> word.displayPinyin
            is HSKWord -> word.displayPinyin
            else -> ""
        }
    }

    private fun getDisplayDefinition(word: Word): String {
        return when (word) {
            is CCWord -> word.definition ?: ""
            is HSKWord -> word.displayDefinition
            else -> ""
        }
    }
}
package com.yonasoft.jadedictionary.features.practice.presentation.state

import com.yonasoft.jadedictionary.features.word.domain.Word

/**
 * Enum defining the different question modes for listening practice
 */
enum class ListeningMode {
    AUDIO_TO_CHARACTER,    // Play audio, guess the character
    AUDIO_TO_PINYIN,       // Play audio, guess the pinyin
    AUDIO_TO_DEFINITION    // Play audio, guess the definition
}

/**
 * Data class representing a listening question
 */
data class ListeningQuestion(
    val word: Word,           // The main word for this question
    val mode: ListeningMode,  // Question type
    val options: List<String>, // Multiple choice options (including the correct answer)
    val correctOptionIndex: Int // Index of the correct option in the options list
)

/**
 * Data class representing the result of answering a listening question
 */
data class ListeningResult(
    val question: ListeningQuestion,
    val selectedOptionIndex: Int?, // User's selected option (null if skipped)
    val isCorrect: Boolean         // Whether the answer was correct
)

/**
 * Data class representing the state for Listening practice
 */
data class ListeningState(
    // Word pool for questions
    val words: List<Word> = emptyList(),

    // Questions generated from the word pool
    val questions: List<ListeningQuestion> = emptyList(),

    // Current question index
    val currentQuestionIndex: Int = 0,

    // Number of options per question
    val optionsPerQuestion: Int = 4,

    // Results for answered questions
    val results: List<ListeningResult> = emptyList(),

    // User's selected option for the current question
    val selectedOptionIndex: Int = -1,

    // Whether the audio is currently playing
    val isAudioPlaying: Boolean = false,

    // Whether the answer has been revealed
    val isAnswerRevealed: Boolean = false,

    // Whether all questions have been answered
    val isPracticeComplete: Boolean = false,

    // Loading state
    val isLoading: Boolean = true,

    // Error message
    val errorMessage: String? = null
) {
    // Helper properties
    val totalQuestions: Int = questions.size
    val progress: Float = if (totalQuestions == 0) 0f else currentQuestionIndex.toFloat() / totalQuestions

    // Get current question safely
    fun getCurrentQuestion(): ListeningQuestion? {
        return if (currentQuestionIndex < questions.size) {
            questions[currentQuestionIndex]
        } else {
            null
        }
    }
}
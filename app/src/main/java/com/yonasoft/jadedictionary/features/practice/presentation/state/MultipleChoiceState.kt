package com.yonasoft.jadedictionary.features.practice.presentation.state

import com.yonasoft.jadedictionary.features.word.domain.Word

/**
 * Enum defining the different question modes for multiple choice practice
 */
enum class QuestionMode {
    PINYIN_TO_CHARACTER,     // Show pinyin, guess the character
    CHARACTER_TO_PINYIN,     // Show character, guess the pinyin
    CHARACTER_TO_DEFINITION, // Show character, guess the definition
    DEFINITION_TO_CHARACTER  // Show definition, guess the character
}

/**
 * Data class representing a multiple choice question
 */
data class MultipleChoiceQuestion(
    val word: Word,           // The main word for this question
    val mode: QuestionMode,   // Question type
    val options: List<String>, // Multiple choice options (including the correct answer)
    val correctOptionIndex: Int // Index of the correct option in the options list
)

/**
 * Data class representing the result of answering a question
 */
data class QuestionResult(
    val question: MultipleChoiceQuestion,
    val selectedOptionIndex: Int?, // User's selected option (null if skipped)
    val isCorrect: Boolean         // Whether the answer was correct
)

/**
 * Data class representing the state for Multiple Choice practice
 */
data class MultipleChoiceState(
    // Word pool for questions
    val words: List<Word> = emptyList(),

    // Questions generated from the word pool
    val questions: List<MultipleChoiceQuestion> = emptyList(),

    // Current question index
    val currentQuestionIndex: Int = 0,

    // Number of options per question
    val optionsPerQuestion: Int = 4,

    // Results for answered questions
    val results: List<QuestionResult> = emptyList(),

    // User's selected option for the current question
    val selectedOptionIndex: Int = -1,

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
    fun getCurrentQuestion(): MultipleChoiceQuestion? {
        return if (currentQuestionIndex < questions.size) {
            questions[currentQuestionIndex]
        } else {
            null
        }
    }
}
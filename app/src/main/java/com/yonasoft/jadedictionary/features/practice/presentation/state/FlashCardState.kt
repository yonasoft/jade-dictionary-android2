package com.yonasoft.jadedictionary.features.practice.presentation.state

import com.yonasoft.jadedictionary.features.word.domain.Word

data class FlashCardState(
    val isLoading: Boolean = true,
    val words: List<Word> = emptyList(),
    val currentWordIndex: Int = 0,
    val easyWords: List<WordResult> = emptyList(),
    val mediumWords: List<WordResult> = emptyList(),
    val hardWords: List<WordResult> = emptyList(),
    val totalWords: Int = 0,
    val isCardFlipped: Boolean = false,
    val isPracticeComplete: Boolean = false,
    val resultsTabIndex: Int = 0,
    val currentCardMode: CardDisplayMode = CardDisplayMode.CHARACTERS
) {
    fun getCurrentWord(): Word? {
        return if (currentWordIndex < words.size) words[currentWordIndex] else null
    }

    val progress: Float
        get() = if (totalWords == 0) 0f else currentWordIndex.toFloat() / totalWords.toFloat()
}

data class WordResult(
    val word: Word,
    val difficulty: WordDifficulty
)

enum class WordDifficulty {
    EASY, MEDIUM, HARD
}

enum class CardDisplayMode {
    CHARACTERS, PINYIN, DEFINITION
}

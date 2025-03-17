package com.yonasoft.jadedictionary.features.word_lists.presentation.state

import com.yonasoft.jadedictionary.features.word.domain.Word
import com.yonasoft.jadedictionary.features.word_lists.domain.WordList

data class WordListDetailState(
    // Word list data
    val wordList: WordList? = null,
    val words: List<Word> = emptyList(),
    val filteredWords: List<Word> = emptyList(),

    // UI state
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",

    // Edit state
    val editTitle: String = "",
    val editDescription: String = "",

    // Undo state
    val lastRemovedWord: Word? = null,
    val isUndoAvailable: Boolean = false,

    // Type indicator
    val isHSKList: Boolean = false
)
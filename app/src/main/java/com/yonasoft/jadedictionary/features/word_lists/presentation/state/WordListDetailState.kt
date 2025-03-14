package com.yonasoft.jadedictionary.features.word_lists.presentation.state

import com.yonasoft.jadedictionary.core.words.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList

data class WordListDetailState(
    val wordList: CCWordList? = null,
    val words: List<CCWord> = emptyList(),
    val filteredWords: List<CCWord> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val editTitle: String = "",
    val editDescription: String = "",
    val errorMessage: String? = null
)
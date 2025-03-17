package com.yonasoft.jadedictionary.features.word_lists.presentation.state

import com.yonasoft.jadedictionary.features.word_lists.domain.WordList
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import com.yonasoft.jadedictionary.features.word_lists.domain.hsk.HSKWordList

data class WordListsState(
    val searchQuery: String = "",
    val myWordLists: List<CCWordList> = emptyList(),
    val hskOldWordLists: List<HSKWordList> = emptyList(),
    val hskNewWordLists: List<HSKWordList> = emptyList(),
    val presetWordLists: List<WordList> = emptyList(),
    val isLoading: Boolean = false
)
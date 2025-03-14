package com.yonasoft.jadedictionary.features.word_lists.presentation.state

import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList

data class WordListsState(
    val searchQuery: String = "",
    val myWordLists: List<CCWordList> = emptyList(),
    val isLoading: Boolean = false
)
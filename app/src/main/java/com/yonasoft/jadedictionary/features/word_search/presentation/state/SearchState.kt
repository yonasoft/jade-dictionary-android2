package com.yonasoft.jadedictionary.features.word_search.presentation.state

import com.yonasoft.jadedictionary.core.words.domain.cc.CCWord

data class SearchState(
    val query: String = "",
    val results: List<CCWord> = emptyList()
)

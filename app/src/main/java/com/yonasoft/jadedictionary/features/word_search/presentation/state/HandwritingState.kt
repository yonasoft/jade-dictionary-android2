package com.yonasoft.jadedictionary.features.word_search.presentation.state

data class HandwritingState(
    val showSheet: Boolean = false,
    val suggestedWords: List<String> = emptyList(),
    val resetCanvasSignal: Long = 0L
)
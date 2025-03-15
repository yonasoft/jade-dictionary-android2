package com.yonasoft.jadedictionary.features.handwriting.presentation.state

data class HandwritingState(
    val showSheet: Boolean = false,
    val suggestedWords: List<String> = emptyList(),
    val resetCanvasSignal: Long = 0L
)
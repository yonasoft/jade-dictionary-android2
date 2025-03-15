package com.yonasoft.jadedictionary.features.word_lists.presentation.state

data class UIState(
    val selectedTab: Int = 2,
    val showCreateDialog: Boolean = false,
    val errorMessage: String? = null
)
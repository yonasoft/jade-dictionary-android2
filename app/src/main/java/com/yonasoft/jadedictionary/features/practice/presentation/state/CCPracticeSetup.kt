package com.yonasoft.jadedictionary.features.practice.presentation.state

import com.yonasoft.jadedictionary.features.practice.domain.models.shared.PracticeType
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord

data class CCPracticeSetupState(
    // Practice configuration
    val practiceType: PracticeType? = null,

    // Word management
    val selectedWords: List<CCWord> = emptyList(),
    val lastRemovedWord: CCWord? = null,
    val isUndoAvailable: Boolean = false,

    // Modal states
    val isSearchModalOpen: Boolean = false,
    val isWordListModalOpen: Boolean = false,

    // Search state
    val searchQuery: String = "",
    val searchResults: List<CCWord> = emptyList(),

    // Loading and error states
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
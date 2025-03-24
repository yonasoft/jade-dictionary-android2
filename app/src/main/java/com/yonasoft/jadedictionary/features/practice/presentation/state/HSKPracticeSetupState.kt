package com.yonasoft.jadedictionary.features.practice.presentation.state

import com.yonasoft.jadedictionary.features.practice.domain.models.shared.PracticeType
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKVersion
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord

data class HSKPracticeSetupState(
    // Practice configuration
    val practiceType: PracticeType? = null,

    // HSK level selection
    val selectedHSKVersion: HSKVersion = HSKVersion.NEW,
    val selectedHSKLevels: List<Int> = emptyList(),
    val availableWordCount: Int = 0,
    val randomWordCount: Int = 0,

    // Word management
    val selectedWords: List<HSKWord> = emptyList(),
    val lastRemovedWord: HSKWord? = null,
    val isUndoAvailable: Boolean = false,

    // Modal states
    val isHSKLevelModalOpen: Boolean = false,
    val isCountSelectorOpen: Boolean = false,

    // Loading and error states
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
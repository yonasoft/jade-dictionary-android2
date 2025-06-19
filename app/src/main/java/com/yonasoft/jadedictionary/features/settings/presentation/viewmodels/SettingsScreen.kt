package com.yonasoft.jadedictionary.features.settings.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.yonasoft.jadedictionary.features.settings.presentation.screens.state.SettingsGraphicalState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsGraphicalState())
    val uiState: StateFlow<SettingsGraphicalState> = _uiState.asStateFlow()
}
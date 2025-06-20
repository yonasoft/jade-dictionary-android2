package com.yonasoft.jadedictionary.features.settings.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.core.constants.DayOfWeek
import com.yonasoft.jadedictionary.core.stores.settings.ThemePreferences
import com.yonasoft.jadedictionary.features.settings.presentation.state.NotificationSettings
import com.yonasoft.jadedictionary.features.settings.presentation.state.SettingsGraphicalState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

class SettingsViewModel(private val themePreferences: ThemePreferences) : ViewModel() {
    private val _graphicalState = MutableStateFlow(SettingsGraphicalState())
    val uiState: StateFlow<SettingsGraphicalState> = _graphicalState.asStateFlow()

    private val _notificationSettings = MutableStateFlow(NotificationSettings())
    val notificationSettings: StateFlow<NotificationSettings> = _notificationSettings.asStateFlow()

    private val _showNotificationDialog = MutableStateFlow(false)
    val showNotificationDialog: StateFlow<Boolean> = _showNotificationDialog.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Main) {
            themePreferences.isDarkTheme.collect { isDark ->
                _graphicalState.value = _graphicalState.value.copy(isDarkMode = isDark)
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch(Dispatchers.Main) {
            themePreferences.setDarkTheme(!_graphicalState.value.isDarkMode)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationSettings.value = _notificationSettings.value.copy(isEnabled = enabled)
    }

    fun openNotificationDialog() {
        _showNotificationDialog.value = true
    }

    fun closeNotificationDialog() {
        _showNotificationDialog.value = false
    }

    fun updateNotificationTime(time: LocalTime) {
        _notificationSettings.value = _notificationSettings.value.copy(time = time)
    }

    fun toggleDay(day: DayOfWeek) {
        val currentDays = _notificationSettings.value.selectedDays.toMutableSet()
        if (currentDays.contains(day)) {
            currentDays.remove(day)
        } else {
            currentDays.add(day)
        }
        _notificationSettings.value = _notificationSettings.value.copy(selectedDays = currentDays)
    }

    fun saveNotificationSettings() {
        // Here you would typically save to DataStore or SharedPreferences
        closeNotificationDialog()
    }
}
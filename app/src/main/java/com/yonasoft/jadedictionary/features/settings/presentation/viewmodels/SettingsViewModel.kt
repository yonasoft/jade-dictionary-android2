package com.yonasoft.jadedictionary.features.settings.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.core.constants.DayOfWeek
import com.yonasoft.jadedictionary.core.notifications.firebase.FirebaseManager
import com.yonasoft.jadedictionary.core.notifications.reminder.ReminderScheduler
import com.yonasoft.jadedictionary.core.stores.settings.ReminderPreferences
import com.yonasoft.jadedictionary.core.stores.settings.ThemePreferences
import com.yonasoft.jadedictionary.features.settings.presentation.state.SettingsGraphicalState
import com.yonasoft.jadedictionary.features.settings.presentation.state.SettingsReminderState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalTime

class SettingsViewModel(
    private val themePreferences: ThemePreferences,
    private val reminderPreferences: ReminderPreferences,
    private val reminderScheduler: ReminderScheduler,
    private val firebaseManager: FirebaseManager
) : ViewModel() {

    private val _graphicalState = MutableStateFlow(SettingsGraphicalState())
    val uiState: StateFlow<SettingsGraphicalState> = _graphicalState.asStateFlow()

    private val _settingsReminderState = MutableStateFlow(SettingsReminderState())
    val settingsReminderState: StateFlow<SettingsReminderState> =
        _settingsReminderState.asStateFlow()

    private val _showNotificationDialog = MutableStateFlow(false)
    val showNotificationDialog: StateFlow<Boolean> = _showNotificationDialog.asStateFlow()

    private val _fcmToken = MutableStateFlow<String?>(null)
    val fcmToken: StateFlow<String?> = _fcmToken.asStateFlow()

    private val _notificationSubscriptions = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val notificationSubscriptions: StateFlow<Map<String, Boolean>> = _notificationSubscriptions.asStateFlow()


    init {
        viewModelScope.launch {
            themePreferences.isDarkTheme.collect { isDark ->
                _graphicalState.value = _graphicalState.value.copy(isDarkMode = isDark)
            }
        }

        viewModelScope.launch {
            combine(
                reminderPreferences.isNotificationsEnabled,
                reminderPreferences.notificationTime,
                reminderPreferences.selectedDays
            ) { enabled, time, days ->
                SettingsReminderState(
                    isEnabled = enabled,
                    time = time,
                    selectedDays = days
                )
            }.collect { settings ->
                _settingsReminderState.value = settings
            }
        }

        loadFCMToken()
        loadNotificationSubscriptions()
    }

    fun toggleTheme() {
        viewModelScope.launch {
            themePreferences.setDarkTheme(!_graphicalState.value.isDarkMode)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            reminderPreferences.setNotificationsEnabled(enabled)

            if (enabled && _settingsReminderState.value.selectedDays.isNotEmpty()) {
                // Schedule notifications if enabled and days are selected
                reminderScheduler.scheduleNotifications(
                    _settingsReminderState.value.time,
                    _settingsReminderState.value.selectedDays
                )
            } else {
                // Cancel all notifications if disabled
                reminderScheduler.cancelAllNotifications()
            }
        }
    }

    fun openNotificationDialog() {
        _showNotificationDialog.value = true
    }

    fun closeNotificationDialog() {
        _showNotificationDialog.value = false
    }

    fun updateNotificationTime(time: LocalTime) {
        viewModelScope.launch {
            reminderPreferences.setNotificationTime(time)
        }
    }

    fun toggleDay(day: DayOfWeek) {
        val currentDays = _settingsReminderState.value.selectedDays.toMutableSet()
        if (currentDays.contains(day)) {
            currentDays.remove(day)
        } else {
            currentDays.add(day)
        }

        viewModelScope.launch {
            reminderPreferences.setSelectedDays(currentDays)
        }
    }

    fun saveNotificationSettings() {
        viewModelScope.launch {
            val settings = _settingsReminderState.value

            // Update scheduling based on current settings
            if (settings.isEnabled && settings.selectedDays.isNotEmpty()) {
                reminderScheduler.scheduleNotifications(settings.time, settings.selectedDays)
            } else {
                reminderScheduler.cancelAllNotifications()
            }

            closeNotificationDialog()
        }
    }

    private fun loadFCMToken() {
        viewModelScope.launch {
            val token = firebaseManager.getToken()
            _fcmToken.value = token
        }
    }

    fun subscribeToTopic(topic: String) {
        firebaseManager.subscribeToTopic(topic)
        updateSubscriptionState(topic, true)
    }

    fun unsubscribeFromTopic(topic: String) {
        firebaseManager.unsubscribeFromTopic(topic)
        updateSubscriptionState(topic, false)
    }

    private fun updateSubscriptionState(topic: String, subscribed: Boolean) {
        val currentSubs = _notificationSubscriptions.value.toMutableMap()
        currentSubs[topic] = subscribed
        _notificationSubscriptions.value = currentSubs
    }

    private fun loadNotificationSubscriptions() {
        // Load saved subscription states (you might want to save these in SharedPreferences)
        val defaultSubscriptions = mapOf(
            "general" to true,
            "practice_reminders" to true,
            "new_features" to false,
            "weekly_summary" to false
        )
        _notificationSubscriptions.value = defaultSubscriptions
    }

    fun refreshFCMToken() {
        loadFCMToken()
    }
}
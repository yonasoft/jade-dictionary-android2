package com.yonasoft.jadedictionary.core.stores.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yonasoft.jadedictionary.core.constants.DayOfWeek
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime

private val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_preferences")

class ReminderPreferences(private val context: Context) {
    companion object {
        private val IS_NOTIFICATIONS_ENABLED = booleanPreferencesKey("is_notifications_enabled")
        private val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        private val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
        private val SELECTED_DAYS = stringSetPreferencesKey("selected_days")
    }

    val isNotificationsEnabled: Flow<Boolean> = context.notificationDataStore.data
        .map { preferences ->
            preferences[IS_NOTIFICATIONS_ENABLED] ?: false
        }

    val notificationTime: Flow<LocalTime> = context.notificationDataStore.data
        .map { preferences ->
            val hour = preferences[NOTIFICATION_HOUR] ?: 9
            val minute = preferences[NOTIFICATION_MINUTE] ?: 0
            LocalTime.of(hour, minute)
        }

    val selectedDays: Flow<Set<DayOfWeek>> = context.notificationDataStore.data
        .map { preferences ->
            val dayNames = preferences[SELECTED_DAYS] ?: emptySet()
            dayNames.mapNotNull { dayName ->
                try {
                    DayOfWeek.valueOf(dayName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }.toSet()
        }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { preferences ->
            preferences[IS_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setNotificationTime(time: LocalTime) {
        context.notificationDataStore.edit { preferences ->
            preferences[NOTIFICATION_HOUR] = time.hour
            preferences[NOTIFICATION_MINUTE] = time.minute
        }
    }

    suspend fun setSelectedDays(days: Set<DayOfWeek>) {
        context.notificationDataStore.edit { preferences ->
            preferences[SELECTED_DAYS] = days.map { it.name }.toSet()
        }
    }
}
package com.yonasoft.jadedictionary.features.settings.presentation.state


import com.yonasoft.jadedictionary.core.constants.DayOfWeek
import java.time.LocalTime

data class NotificationSettings(
    val isEnabled: Boolean = false,
    val selectedDays: Set<DayOfWeek> = emptySet(),
    val time: LocalTime = LocalTime.of(9, 0) // Default 9:00 AM
)
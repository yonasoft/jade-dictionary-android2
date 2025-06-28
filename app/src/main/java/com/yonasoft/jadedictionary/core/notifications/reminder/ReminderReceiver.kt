package com.yonasoft.jadedictionary.core.notifications.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationScheduler = ReminderScheduler(context)
        notificationScheduler.showImmediateNotification()
    }
}
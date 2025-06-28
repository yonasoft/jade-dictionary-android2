package com.yonasoft.jadedictionary.core.notifications.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yonasoft.jadedictionary.MainActivity
import com.yonasoft.jadedictionary.R

class JadeFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCM"
        private const val CHANNEL_ID = "jade_notifications"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "Data payload: ${remoteMessage.data}")

        // Check if message contains a data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Message Notification Body: ${notification.body}")
            showNotification(
                title = notification.title ?: "Jade Dictionary",
                body = notification.body ?: "",
                data = remoteMessage.data
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        sendTokenToServer(token)
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"] ?: "Jade Dictionary"
        val body = data["body"] ?: ""
        showNotification(title, body, data)
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        Log.d(TAG, "Creating notification with data: $data")

        val pendingIntent = createNotificationPendingIntent(data)
        Log.d(TAG, "Created pending intent")

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.jade_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Changed to HIGH
            .setCategory(NotificationCompat.CATEGORY_PROMO) // Added category

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

        Log.d(TAG, "Notification shown")
    }

    private fun createNotificationPendingIntent(data: Map<String, String>): PendingIntent {
        val action = data["action"]
        val url = data["url"]

        Log.d(TAG, "Processing action: $action, url: $url")

        // Handle direct Play Store URLs
        if (!url.isNullOrEmpty() && url.contains("play.google.com")) {
            Log.d(TAG, "Creating Play Store intent for URL: $url")

            // Create Play Store intent that will definitely work
            val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                setData(Uri.parse("market://details?id=com.yonasoft.jadedictionary"))
                setPackage("com.android.vending")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            // Try Play Store app first, fallback to browser
            return try {
                PendingIntent.getActivity(
                    this,
                    System.currentTimeMillis().toInt(), // Unique request code
                    playStoreIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } catch (e: Exception) {
                Log.w(TAG, "Play Store app not available, using browser", e)
                val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                    setData(Uri.parse(url))
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                PendingIntent.getActivity(
                    this,
                    System.currentTimeMillis().toInt(),
                    browserIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        }

        // Handle other URLs
        if (!url.isNullOrEmpty()) {
            Log.d(TAG, "Creating generic URL intent for: $url")
            val urlIntent = Intent(Intent.ACTION_VIEW).apply {
                setData(Uri.parse(url))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            return PendingIntent.getActivity(
                this,
                System.currentTimeMillis().toInt(),
                urlIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        // Handle app-specific actions
        Log.d(TAG, "Creating app intent for action: $action")
        val appIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

            when (action) {
                "open_play_store", "rate_app" -> {
                    putExtra("open_play_store", true)
                    putExtra("action", action)
                    putExtra("from_notification", true)
                }
                "share_app" -> {
                    putExtra("share_app", true)
                    putExtra("from_notification", true)
                }
                "practice" -> {
                    putExtra("navigate_to_practice", true)
                    putExtra("from_notification", true)
                }
                "word_detail" -> {
                    data["word_id"]?.let { wordId ->
                        putExtra("navigate_to_word", wordId)
                        putExtra("word_source", data["word_source"] ?: "CC")
                    }
                    putExtra("from_notification", true)
                }
            }

            // Add all data as extras for debugging
            data.forEach { (key, value) ->
                putExtra("fcm_$key", value)
            }
        }

        return PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(), // Unique request code
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Jade Dictionary Notifications",
            NotificationManager.IMPORTANCE_HIGH // Changed to HIGH
        ).apply {
            description = "Notifications for practice reminders and updates"
            enableLights(true)
            enableVibration(true)
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendTokenToServer(token: String) {
        Log.d(TAG, "Token to send to server: $token")
        val sharedPref = getSharedPreferences("firebase_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("fcm_token", token)
            apply()
        }
    }
}
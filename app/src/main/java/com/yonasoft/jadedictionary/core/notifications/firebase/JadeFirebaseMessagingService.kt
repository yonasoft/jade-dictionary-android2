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

        val intent = createNotificationIntent(data)
        Log.d(TAG, "Created intent with extras: ${intent.extras}")

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.jade_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

        Log.d(TAG, "Notification shown")
    }

    private fun createNotificationIntent(data: Map<String, String>): Intent {
        val action = data["action"]
        val url = data["url"]

        Log.d(TAG, "Processing action: $action")
        Log.d(TAG, "Processing url: $url")

        // If there's a URL, create direct intent to open it
        if (!url.isNullOrEmpty()) {
            Log.d(TAG, "Creating direct URL intent for: $url")
            return try {
                if (url.contains("play.google.com")) {
                    // Try Play Store app first
                    Intent(Intent.ACTION_VIEW).apply {
                        setData(Uri.parse("market://details?id=com.yonasoft.jadedictionary"))
                        setPackage("com.android.vending")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                } else {
                    // Generic URL
                    Intent(Intent.ACTION_VIEW).apply {
                        setData(Uri.parse(url))
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create URL intent, falling back to web", e)
                // Fallback to web browser
                Intent(Intent.ACTION_VIEW).apply {
                    setData(Uri.parse(url))
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
        }

        // If no URL, use app intent with data
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        when (action) {
            "open_play_store", "rate_app" -> {
                Log.d(TAG, "Setting open_play_store extra")
                intent.putExtra("open_play_store", true)
                intent.putExtra("action", action)
                intent.putExtra("from_notification", true)
            }
            "share_app" -> {
                intent.putExtra("share_app", true)
                intent.putExtra("from_notification", true)
            }
            "practice" -> {
                intent.putExtra("navigate_to_practice", true)
            }
            "word_detail" -> {
                data["word_id"]?.let { wordId ->
                    intent.putExtra("navigate_to_word", wordId)
                    intent.putExtra("word_source", data["word_source"] ?: "CC")
                }
            }
        }

        // Add all data as extras for debugging
        data.forEach { (key, value) ->
            intent.putExtra("data_$key", value)
        }

        Log.d(TAG, "Intent created with extras: ${intent.extras?.keySet()}")
        return intent
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Jade Dictionary Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for practice reminders and updates"
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
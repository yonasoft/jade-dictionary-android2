package com.yonasoft.jadedictionary.core.notifications.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class FirebaseManager(private val context: Context) {

    companion object {
        private const val TAG = "FirebaseManager"
    }

    suspend fun getToken(): String? {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "FCM Token: $token")

            // Save token locally
            saveTokenLocally(token)
            token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get FCM token", e)
            null
        }
    }

    fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Subscribed to topic: $topic"
                if (!task.isSuccessful) {
                    msg = "Failed to subscribe to topic: $topic"
                }
                Log.d(TAG, msg)
            }
    }

    fun unsubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Unsubscribed from topic: $topic"
                if (!task.isSuccessful) {
                    msg = "Failed to unsubscribe from topic: $topic"
                }
                Log.d(TAG, msg)
            }
    }

    private fun saveTokenLocally(token: String) {
        val sharedPref = context.getSharedPreferences("firebase_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("fcm_token", token)
            apply()
        }
    }

    fun getStoredToken(): String? {
        val sharedPref = context.getSharedPreferences("firebase_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("fcm_token", null)
    }
}
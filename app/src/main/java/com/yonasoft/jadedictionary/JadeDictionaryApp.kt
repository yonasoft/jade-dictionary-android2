// Replace your current JadeDictionaryApp.kt with this:
package com.yonasoft.jadedictionary

import android.app.Application
import android.util.Log
import com.github.promeg.pinyinhelper.Pinyin
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict
import com.google.firebase.messaging.FirebaseMessaging
import com.yonasoft.jadedictionary.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class JadeDictionaryApp : Application() {

    companion object {
        private const val TAG = "JadeDictionaryApp"
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "Application starting...")
        Log.d(TAG, "Package name: ${packageName}")

        // Initialize Koin
        startKoin {
            androidContext(this@JadeDictionaryApp)
            modules(appModule)
        }

        // Initialize Pinyin
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(this@JadeDictionaryApp)))

        // Try to initialize Firebase directly
        initializeFirebaseDirectly()
    }

    private fun initializeFirebaseDirectly() {
        try {
            Log.d(TAG, "Attempting Firebase initialization...")

            // Try to initialize Firebase
            val firebaseApp = com.google.firebase.FirebaseApp.initializeApp(this)

            if (firebaseApp != null) {
                Log.d(TAG, "✓ Firebase initialized successfully!")
                Log.d(TAG, "✓ App name: ${firebaseApp.name}")
                Log.d(TAG, "✓ Project ID: ${firebaseApp.options.projectId}")
                Log.d(TAG, "✓ Application ID: ${firebaseApp.options.applicationId}")
                Log.d(TAG, "✓ API Key: ${firebaseApp.options.apiKey.take(10)}...")

                // Now try FCM
                getFCMToken()
            } else {
                Log.e(TAG, "✗ Firebase initialization returned null")
            }

        } catch (e: Exception) {
            Log.e(TAG, "✗ Firebase initialization failed", e)
            Log.e(TAG, "This usually means:")
            Log.e(TAG, "1. google-services.json is missing or in wrong location")
            Log.e(TAG, "2. Google Services plugin not applied")
            Log.e(TAG, "3. Wrong package name in Firebase project")
            Log.e(TAG, "4. Need to sync Gradle")
        }
    }

    private fun getFCMToken() {
        try {
            Log.d(TAG, "Getting FCM token...")

            com.google.firebase.messaging.FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "✗ Getting FCM token failed", task.exception)
                        return@addOnCompleteListener
                    }

                    val token = task.result
                    Log.d(TAG, "✓ FCM Token SUCCESS!")
                    Log.d(TAG, "=================================")
                    Log.d(TAG, "FCM TOKEN: $token")
                    Log.d(TAG, "=================================")

                    // Save for easy access
                    saveToken(token)
                }

            FirebaseMessaging.getInstance().subscribeToTopic("test_only")
        } catch (e: Exception) {
            Log.e(TAG, "✗ FCM setup failed", e)
        }
    }

    private fun saveToken(token: String) {
        try {
            val prefs = getSharedPreferences("fcm_debug", MODE_PRIVATE)
            prefs.edit().putString("token", token).apply()
            Log.d(TAG, "✓ Token saved to SharedPreferences")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save token", e)
        }
    }
}
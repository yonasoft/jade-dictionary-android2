package com.yonasoft.jadedictionary.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

object PlayStoreUtils {

    private const val TAG = "PlayStoreUtils"
    private const val PACKAGE_NAME = "com.yonasoft.jadedictionary"

    /**
     * Opens the app's Play Store page
     * First tries to open in Play Store app, falls back to web browser
     */
    fun openPlayStorePage(context: Context) {
        try {
            // Try to open in Play Store app first
            val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=$PACKAGE_NAME")
                setPackage("com.android.vending")
            }

            context.startActivity(playStoreIntent)
            Log.d(TAG, "Opened Play Store app")

        } catch (e: Exception) {
            // Fallback to web browser if Play Store app not available
            try {
                val webIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=$PACKAGE_NAME")
                }
                context.startActivity(webIntent)
                Log.d(TAG, "Opened Play Store in browser")

            } catch (webException: Exception) {
                Log.e(TAG, "Failed to open Play Store", webException)
            }
        }
    }

    /**
     * Opens Play Store to rate the app
     */
    fun openRateApp(context: Context) {
        openPlayStorePage(context)
    }

    /**
     * Opens Play Store to share the app
     */
    fun shareApp(context: Context) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Check out Jade Dictionary")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Hey! Check out this amazing Chinese dictionary app: https://play.google.com/store/apps/details?id=$PACKAGE_NAME"
                )
            }

            val chooser = Intent.createChooser(shareIntent, "Share Jade Dictionary")
            context.startActivity(chooser)
            Log.d(TAG, "Opened share dialog")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to share app", e)
        }
    }

    /**
     * Check if Play Store app is installed
     */
    fun isPlayStoreInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.android.vending", 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}
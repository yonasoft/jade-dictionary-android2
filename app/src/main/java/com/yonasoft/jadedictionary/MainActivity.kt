package com.yonasoft.jadedictionary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.yonasoft.jadedictionary.features.home.presentation.screens.Home
import com.yonasoft.jadedictionary.ui.theme.JadeDictionaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            JadeDictionaryTheme {
                Home()
            }
        }
    }
}

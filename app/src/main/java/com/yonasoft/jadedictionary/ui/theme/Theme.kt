// Updated theme with status bar text color adjustments
package com.yonasoft.jadedictionary.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.yonasoft.jadedictionary.core.constants.CustomColor

// Updated dark color scheme that uses your app's dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = CustomColor.GREEN01.color,  // Use your app's accent color
    secondary = Color(0xFF1A237E),  // Blue accent
    tertiary = CustomColor.GREEN01.color,
    background = Color(0xFF0A0A0A),  // Your app's dark background
    surface = Color(0xFF121212),     // Your app's card background
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

// Light color scheme in case you ever need it
private val LightColorScheme = lightColorScheme(
    primary = CustomColor.GREEN01.color,
    secondary = Color(0xFF1A237E),
    tertiary = CustomColor.GREEN01.color,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun JadeDictionaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Force dark theme for your app regardless of system settings
    forceDarkTheme: Boolean = true,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled by default to maintain your custom colors
    content: @Composable () -> Unit
) {
    // Use dark theme by default for your app
    val useDarkTheme = forceDarkTheme || darkTheme

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Set up status bar colors and appearance
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Set the status bar background color
            window.statusBarColor = Color(0xFF050505).toArgb() // Slightly darker than your app background

            // Ensure the status bar icons and text are white (visible on dark background)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
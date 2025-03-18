package com.yonasoft.jadedictionary.core.constants

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

enum class CustomColor(val color: Color) {
    GREEN01(lerp(Color.Green, Color.White, .5f))
}

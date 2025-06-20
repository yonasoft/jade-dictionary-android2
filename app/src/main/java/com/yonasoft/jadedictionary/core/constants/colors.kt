package com.yonasoft.jadedictionary.core.constants

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

enum class CustomColor(val color: Color) {
    GREEN01(lerp(Color.Green, Color.White, .4f)),
    DARK01(Color(0xFF0A0A0A)),
    DARK02(Color(0xFF050505))
    ;
}

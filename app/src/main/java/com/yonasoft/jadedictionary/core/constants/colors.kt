package com.yonasoft.jadedictionary.core.constants

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

enum class CustomColor(val color: Color) {
    GRAY01(Color(0xFFBAC1B8)),
    GRAY02(Color(0xFFE5E5E5)),
    GRAY03(Color(0xFF2E2E2E)),
    GRAY04(lerp(Color.Black, Color.White, 0.2f)),
    GREEN01(lerp(Color.Green, Color.White, .5f))
}

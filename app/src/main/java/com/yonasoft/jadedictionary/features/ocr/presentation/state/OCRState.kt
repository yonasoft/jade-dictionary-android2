package com.yonasoft.jadedictionary.features.ocr.presentation.state

import android.graphics.Bitmap

data class OCRState(
    val showSheet: Boolean = false,
    val results: List<String> = emptyList(),
    val currentImage: Bitmap? = null
)

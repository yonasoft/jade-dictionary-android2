package com.yonasoft.jadedictionary.features.practice.domain.models.main

import androidx.compose.ui.graphics.vector.ImageVector

data class PracticeOption(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isLocked: Boolean = false,
    val route: String? = null
)
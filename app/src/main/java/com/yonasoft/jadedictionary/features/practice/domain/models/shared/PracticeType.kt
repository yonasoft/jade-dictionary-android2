package com.yonasoft.jadedictionary.features.practice.domain.models.shared

enum class PracticeType(val routeKey: String, val displayName: String) {
    FLASH_CARDS("flash_cards", "Flash Cards"),
    MULTIPLE_CHOICE("multiple_choice", "Multiple Choice"),
    LISTENING("listening", "Listening");

    companion object {
        fun fromRouteKey(key: String): PracticeType? {
            return entries.find { it.routeKey == key }
        }
    }
}
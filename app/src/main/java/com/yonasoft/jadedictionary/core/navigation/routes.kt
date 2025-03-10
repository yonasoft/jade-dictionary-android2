package com.yonasoft.jadedictionary.core.navigation

enum class MainRoutes {
    Home,
    Words
}

sealed class WordRoutes(val route: String) {
    data object WordSearch : WordRoutes("word_search")
    data object WordDetail : WordRoutes("word_detail/{wordId}") {
        fun createRoute(wordId: Long) = "word_detail/$wordId"
    }
}
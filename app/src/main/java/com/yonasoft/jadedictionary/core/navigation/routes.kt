package com.yonasoft.jadedictionary.core.navigation

enum class MainRoutes {
    Home,
    Words,
    WordLists,
}

sealed class WordRoutes(val route: String) {
    data object WordSearch : WordRoutes("word_search")
    data object CCWordDetail : WordRoutes("cc_word_detail/{wordId}") {
        fun createRoute(wordId: Long) = "cc_word_detail/$wordId"
    }
    data object HSKWordDetail : WordRoutes("hsk_word_detail/{wordId}") {
        fun createRoute(wordId: Long) = "hsk_word_detail/$wordId"
    }
}

sealed class WordListRoutes(val route: String) {
    data object WordLists : WordListRoutes("word_lists")
    data object WordListDetail : WordListRoutes("word_list_detail/{wordListId}") {
        fun createRoute(wordListId: Long) = "word_list_detail/$wordListId"
    }
}
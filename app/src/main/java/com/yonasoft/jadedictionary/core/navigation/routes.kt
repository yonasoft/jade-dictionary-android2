package com.yonasoft.jadedictionary.core.navigation

import com.yonasoft.jadedictionary.features.practice.domain.models.shared.PracticeType

enum class MainRoutes {
    Home,
    Words,
    WordLists,
    Practice,
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

sealed class PracticeRoutes(val route: String) {
    // Selection screen where user chooses a practice type
    data object PracticeSelection : PracticeRoutes("practice_selection")

    // Setup screen for any practice type
    data object CCPracticeSetup : PracticeRoutes("cc_practice_setup/{practiceType}") {
        fun createRoute(practiceType: PracticeType) = "cc_practice_setup/${practiceType.routeKey}"
    }

    data object HSKPracticeSetup : PracticeRoutes("hsk_practice_setup/{practiceType}") {
        fun createRoute(practiceType: PracticeType) = "hsk_practice_setup/${practiceType.routeKey}"
    }

    // Practice screens
    data object FlashCardPractice : PracticeRoutes("flash_card_practice/{wordSource}/{wordIds}") {
        fun createRoute(wordSource: String, wordIds: List<Long>): String {
            val idsString = wordIds.joinToString(",")
            return "flash_card_practice/$wordSource/$idsString"
        }
    }

    data object MultipleChoicePractice : PracticeRoutes("multiple_choice_practice/{wordSource}/{wordIds}") {
        fun createRoute(wordSource: String, wordIds: List<Long>): String {
            val idsString = wordIds.joinToString(",")
            return "multiple_choice_practice/$wordSource/$idsString"
        }
    }

    data object ListeningPractice : PracticeRoutes("listening_practice/{wordSource}/{wordIds}") {
        fun createRoute(wordSource: String, wordIds: List<Long>): String {
            val idsString = wordIds.joinToString(",")
            return "listening_practice/$wordSource/$idsString"
        }
    }
}
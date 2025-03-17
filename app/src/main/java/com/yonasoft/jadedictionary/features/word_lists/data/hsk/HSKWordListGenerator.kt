package com.yonasoft.jadedictionary.features.word_lists.data.hsk


import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKLevel
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKVersion
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWordRepository
import com.yonasoft.jadedictionary.features.word_lists.domain.hsk.HSKWordList

/**
 * Utility class to generate HSK word lists from HSK words
 */
object HSKWordListGenerator {

    /**
     * Generate HSK word lists for a specific version
     */
    suspend fun generateHSKWordLists(
        repository: HSKWordRepository,
        version: HSKVersion
    ): List<HSKWordList> {
        val result = mutableListOf<HSKWordList>()
        val allWords = repository.getAllWords()

        // Generate a list for each HSK level
        val levels = when (version) {
            HSKVersion.OLD -> listOf(
                HSKLevel.LEVEL1, HSKLevel.LEVEL2, HSKLevel.LEVEL3,
                HSKLevel.LEVEL4, HSKLevel.LEVEL5, HSKLevel.LEVEL6
            )

            HSKVersion.NEW -> listOf(
                HSKLevel.LEVEL1, HSKLevel.LEVEL2, HSKLevel.LEVEL3,
                HSKLevel.LEVEL4, HSKLevel.LEVEL5, HSKLevel.LEVEL6, HSKLevel.LEVEL7
            )
        }

        for (level in levels) {
            val filteredWords = filterWordsByVersionAndLevel(allWords, version, level)
            val id = generateId(version, level)

            result.add(
                HSKWordList(
                    id = id,
                    title = formatTitle(version, level),
                    description = formatDescription(version, level, filteredWords.size),
                    version = version,
                    level = level,
                    wordCount = filteredWords.size
                )
            )
        }

        return result
    }

    /**
     * Filter HSK words by version and level
     */
    private fun filterWordsByVersionAndLevel(
        words: List<HSKWord>,
        version: HSKVersion,
        level: HSKLevel
    ): List<HSKWord> {
        return words.filter { word ->
            when (version) {
                HSKVersion.OLD -> word.hskOldLevel == level.value
                HSKVersion.NEW -> word.hskNewLevel == level.value
            }
        }
    }

    /**
     * Get HSK words for a specific HSK word list
     */
    suspend fun getHSKWordsForList(
        repository: HSKWordRepository,
        wordList: HSKWordList
    ): List<HSKWord> {
        val allWords = repository.getAllWords()
        return filterWordsByVersionAndLevel(allWords, wordList.version, wordList.level)
    }

    /**
     * Generate a unique ID for an HSK word list based on version and level
     */
    private fun generateId(version: HSKVersion, level: HSKLevel): Long {
        // Create IDs that won't conflict with Room-generated IDs (which start from 1)
        // Use a format like: 1000000 + (version * 1000) + level
        val versionCode = when (version) {
            HSKVersion.OLD -> 1
            HSKVersion.NEW -> 2
        }

        return (1000000 + (versionCode * 1000) + level.value).toLong()
    }

    /**
     * Format title for an HSK word list
     */
    private fun formatTitle(version: HSKVersion, level: HSKLevel): String {
        val versionName = when (version) {
            HSKVersion.OLD -> "HSK 2.0"
            HSKVersion.NEW -> "HSK 3.0"
        }

        return "$versionName Level ${if (level.value < 7) level.value else "7-9"}"
    }

    /**
     * Format description for an HSK word list
     */
    private fun formatDescription(version: HSKVersion, level: HSKLevel, wordCount: Int): String {
        val versionName = when (version) {
            HSKVersion.OLD -> "HSK 2.0"
            HSKVersion.NEW -> "HSK 3.0"
        }

        return "Official $versionName Level ${level.value} vocabulary list containing $wordCount words."
    }
}
package com.yonasoft.jadedictionary.features.word.domain.hsk

import com.yonasoft.jadedictionary.features.word.domain.utils.PinyinUtils

object HSKUtils {

    fun sortWords(words: List<HSKWord>, sortBy: SortCriteria): List<HSKWord> {
        return when (sortBy) {
            SortCriteria.FREQUENCY -> words.sortedBy { it.frequency }
            SortCriteria.SIMPLIFIED -> words.sortedBy { it.simplified }
            SortCriteria.TRADITIONAL -> words.sortedBy { it.traditional ?: it.simplified }
            SortCriteria.PINYIN -> words.sortedBy { it.pinyin ?: "" }
            SortCriteria.LEVEL_ASCENDING -> words.sortedWith(
                compareBy<HSKWord> { it.hskNewLevel ?: Int.MAX_VALUE }
                    .thenBy { it.hskOldLevel ?: Int.MAX_VALUE }
            )
            SortCriteria.LEVEL_DESCENDING -> words.sortedWith(
                compareByDescending<HSKWord> { it.hskNewLevel ?: 0 }
                    .thenByDescending { it.hskOldLevel ?: 0 }
            )
        }
    }

    fun searchWords(words: List<HSKWord>, query: String): List<HSKWord> {
        if (query.isBlank()) return words

        val normalizedQuery = PinyinUtils.normalizeQuery(query.trim().lowercase())

        return words.filter { word ->
            word.simplified.lowercase().contains(normalizedQuery) ||
                    (word.traditional?.lowercase()?.contains(normalizedQuery) == true) ||
                    (word.pinyin?.lowercase()?.contains(normalizedQuery) == true) ||
                    (word.numericPinyin?.lowercase()?.contains(normalizedQuery) == true) ||
                    word.definitions.any { it.lowercase().contains(normalizedQuery) }
        }
    }

    fun getHSKLevelDisplay(word: HSKWord): String {
        val parts = mutableListOf<String>()

        word.hskNewLevel?.let {
            val levelText = if (it >= 7) {
                "HSK 3.0: Level $it (7-9)"
            } else {
                "HSK 3.0: Level $it"
            }
            parts.add(levelText)
        }

        word.hskOldLevel?.let { parts.add("HSK 2.0: Level $it") }

        return parts.joinToString(", ")
    }

    fun getWordsUniqueToVersion(words: List<HSKWord>, version: HSKVersion): List<HSKWord> {
        return when (version) {
            HSKVersion.OLD -> words.filter { it.hskOldLevel != null && it.hskNewLevel == null }
            HSKVersion.NEW -> words.filter { it.hskNewLevel != null && it.hskOldLevel == null }
        }
    }

    fun getWordsInBothVersions(words: List<HSKWord>): List<HSKWord> {
        return words.filter { it.hskOldLevel != null && it.hskNewLevel != null }
    }

    fun compareLevels(word: HSKWord): LevelComparison {
        if (word.hskNewLevel == null || word.hskOldLevel == null) return LevelComparison.INCOMPARABLE

        return when {
            word.hskNewLevel > word.hskOldLevel -> LevelComparison.HIGHER_IN_NEW
            word.hskNewLevel < word.hskOldLevel -> LevelComparison.LOWER_IN_NEW
            else -> LevelComparison.SAME
        }
    }

    /**
     * Determines if a word is in the HSK 7-9 advanced level
     */
    fun isAdvancedLevel(word: HSKWord): Boolean {
        return word.hskNewLevel != null && word.hskNewLevel >= 7
    }

    /**
     * Gets words from HSK 7-9 advanced level
     */
    fun getAdvancedLevelWords(words: List<HSKWord>): List<HSKWord> {
        return words.filter { isAdvancedLevel(it) }
    }

    /**
     * Formats the display of HSK level for UI components
     * @param level The HSK level number
     * @param isNew Whether this is from HSK 3.0 (true) or HSK 2.0 (false)
     * @return Formatted display string for the HSK level
     */
    fun formatHSKLevelForDisplay(level: Int, isNew: Boolean): String {
        return if (isNew) {
            if (level >= 7) {
                "HSK3: $level (7-9)"
            } else {
                "HSK3: $level"
            }
        } else {
            "HSK2: $level"
        }
    }

    /**
     * Parse HSK levels from json level information
     * Format in json: ["new-1", "old-3"] or similar
     */
    fun parseHSKLevels(levelStrings: List<String>): Pair<Int?, Int?> {
        var hskNewLevel: Int? = null
        var hskOldLevel: Int? = null

        for (levelString in levelStrings) {
            when {
                levelString.startsWith("new-") -> {
                    val level = levelString.substringAfter("new-").toIntOrNull()
                    if (level != null) {
                        hskNewLevel = level
                    }
                }
                levelString.startsWith("old-") -> {
                    val level = levelString.substringAfter("old-").toIntOrNull()
                    if (level != null) {
                        hskOldLevel = level
                    }
                }
            }
        }

        return Pair(hskNewLevel, hskOldLevel)
    }
}

enum class SortCriteria {
    FREQUENCY,
    SIMPLIFIED,
    TRADITIONAL,
    PINYIN,
    LEVEL_ASCENDING,
    LEVEL_DESCENDING
}

enum class HSKVersion {
    OLD,  // HSK 2.0
    NEW   // HSK 3.0
}

enum class LevelComparison {
    HIGHER_IN_NEW,    // Word is at a higher level in HSK 3.0 than 2.0
    LOWER_IN_NEW,     // Word is at a lower level in HSK 3.0 than 2.0
    SAME,             // Word is at the same level in both versions
    INCOMPARABLE      // Word is only in one version or levels are missing
}
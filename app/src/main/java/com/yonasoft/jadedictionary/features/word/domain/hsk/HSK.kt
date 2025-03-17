package com.yonasoft.jadedictionary.features.word.domain.hsk

/**
 * Enum representing the HSK version (old = HSK 2.0, new = HSK 3.0)
\
 * Enum representing the HSK levels with their string representations from the JSON
 */
enum class HSKLevel(val value: Int, val jsonPrefix: String) {
    LEVEL1(1, "1"),
    LEVEL2(2, "2"),
    LEVEL3(3, "3"),
    LEVEL4(4, "4"),
    LEVEL5(5, "5"),
    LEVEL6(6, "6"),
    LEVEL7(7, "7"); // Only exists for NEW version

    companion object {
        fun fromInt(value: Int): HSKLevel? = values().find { it.value == value }

        fun fromJsonTag(tag: String): Pair<HSKVersion, HSKLevel>? {
            return when {
                tag.startsWith("new-") -> {
                    val level = fromInt(tag.removePrefix("new-").toIntOrNull() ?: return null)
                    level?.let { Pair(HSKVersion.NEW, it) }
                }
                tag.startsWith("old-") -> {
                    val level = fromInt(tag.removePrefix("old-").toIntOrNull() ?: return null)
                    level?.let { Pair(HSKVersion.OLD, it) }
                }
                // Also handle abbreviated format from min.json
                tag.startsWith("n") -> {
                    val level = fromInt(tag.removePrefix("n").toIntOrNull() ?: return null)
                    level?.let { Pair(HSKVersion.NEW, it) }
                }
                tag.startsWith("o") -> {
                    val level = fromInt(tag.removePrefix("o").toIntOrNull() ?: return null)
                    level?.let { Pair(HSKVersion.OLD, it) }
                }
                else -> null
            }
        }
    }
}
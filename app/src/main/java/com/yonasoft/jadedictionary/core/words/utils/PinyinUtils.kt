package com.yonasoft.jadedictionary.core.words.utils

object PinyinUtils {
    private val accentToNumberMap = mapOf(
        "ā" to "a1", "á" to "a2", "ǎ" to "a3", "à" to "a4",
        "ē" to "e1", "é" to "e2", "ě" to "e3", "è" to "e4",
        "ī" to "i1", "í" to "i2", "ǐ" to "i3", "ì" to "i4",
        "ō" to "o1", "ó" to "o2", "ǒ" to "o3", "ò" to "o4",
        "ū" to "u1", "ú" to "u2", "ǔ" to "u3", "ù" to "u4",
        "ǖ" to "v1", "ǘ" to "v2", "ǚ" to "v3", "ǜ" to "v4", "ü" to "v"
    )

    private val numberToAccentMap = accentToNumberMap.entries.associate { (accent, numbered) ->
        numbered to accent
    }

    fun normalizeQuery(query: String): String {
        if (query.isBlank()) return ""

        // If it's Chinese characters, return as is
        if (query.any { it.code in 0x4E00..0x9FFF }) {
            return query.trim()
        }

        var normalized = query.trim().lowercase()

        // Handle 'ü' to 'v' conversion
        normalized = normalized.replace("ü", "v")

        // For spaced numbered pinyin (dian4 shi4), remove extra spaces
        if (normalized.matches(Regex("([a-z]+[1-4]\\s*)+"))){
            return normalized.replace(Regex("\\s+"), " ")
        }

        // For unspaced numbered pinyin (dian4shi4), add spaces
        if (normalized.matches(Regex("[a-z]+[1-4]+"))){
            return normalized.replace(Regex("(?<=[1-4])(?=[a-z])"), " ")
        }

        // For accented pinyin (with or without spaces)
        var hasAccent = false
        accentToNumberMap.forEach { (accent, _) ->
            if (normalized.contains(accent)) {
                hasAccent = true
            }
        }

        if (hasAccent) {
            // Split into syllables if there are spaces
            val parts = if (normalized.contains(" ")) {
                normalized.split(" ")
            } else {
                // Try to split by consonants if no spaces
                listOf(normalized)
            }

            // Convert each part
            val converted = parts.map { part ->
                var syllable = part
                var tone = ""
                accentToNumberMap.forEach { (accent, numbered) ->
                    if (syllable.contains(accent)) {
                        syllable = syllable.replace(accent, numbered[0].toString())
                        tone = numbered[1].toString()
                    }
                }
                "$syllable$tone"
            }

            return converted.joinToString(" ")
        }

        return normalized
    }

    fun numberedToAccented(pinyin: String): String {
        if (pinyin.isBlank()) return ""

        // Split into syllables
        val syllables = pinyin.trim().split(" ")

        return syllables.map { syllable ->
            if (syllable.matches(Regex("[a-z]+[1-5]"))) {
                val tone = syllable.last()
                val base = syllable.substring(0, syllable.length - 1)

                // Find the vowel to accent using updated rules
                val (vowelToAccent, index) = findVowelToAccent(base)
                if (index != -1 && vowelToAccent != null) {
                    val accentedVowel = numberToAccentMap["$vowelToAccent$tone"] ?: vowelToAccent.toString()
                    base.substring(0, index) + accentedVowel + base.substring(index + 1)
                } else {
                    syllable
                }
            } else {
                syllable
            }
        }.joinToString(" ")
    }

    private fun findVowelToAccent(syllable: String): Pair<Char?, Int> {
        // Handle compound vowels first
        val compounds = listOf("iu", "ui", "ia", "ua", "ie", "ue", "üe", "ve")
        compounds.forEach { compound ->
            val index = syllable.indexOf(compound)
            if (index != -1) {
                // For compounds, accent falls on last vowel
                return Pair(compound.last(), index + 1)
            }
        }

        // Handle special cases
        if (syllable.contains("ao")) {
            return Pair('a', syllable.indexOf('a'))
        }
        if (syllable.contains("ou")) {
            return Pair('o', syllable.indexOf('o'))
        }
        if (syllable.contains("ai")) {
            return Pair('a', syllable.indexOf('a'))
        }
        if (syllable.contains("ei")) {
            return Pair('e', syllable.indexOf('e'))
        }

        // Single vowel precedence
        if (syllable.contains('a')) {
            return Pair('a', syllable.indexOf('a'))
        }
        if (syllable.contains('e')) {
            return Pair('e', syllable.indexOf('e'))
        }
        if (syllable.contains('o')) {
            return Pair('o', syllable.indexOf('o'))
        }
        if (syllable.contains('i')) {
            return Pair('i', syllable.indexOf('i'))
        }
        if (syllable.contains('u')) {
            return Pair('u', syllable.indexOf('u'))
        }
        if (syllable.contains('v') || syllable.contains('ü')) {
            return Pair('v', syllable.indexOf(if (syllable.contains('v')) 'v' else 'ü'))
        }

        return Pair(null, -1)
    }

}

// Add this to your repository to debug:

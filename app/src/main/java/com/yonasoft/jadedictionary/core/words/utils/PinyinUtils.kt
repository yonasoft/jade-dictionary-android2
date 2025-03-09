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

    private val pinyinToneMark = mapOf(
        5 to "aoeiuvü",  // Changed from 0 to 5 for neutral tone
        1 to "āōēīūǖǖ",
        2 to "áóéíúǘǘ",
        3 to "ǎǒěǐǔǚǚ",
        4 to "àòèìùǜǜ"
    )

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
        if (normalized.matches(Regex("([a-z]+[1-4]\\s*)+"))) {
            return normalized.replace(Regex("\\s+"), " ")
        }

        // For unspaced numbered pinyin (dian4shi4), add spaces
        if (normalized.matches(Regex("[a-z]+[1-4]+"))) {
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

    fun decodePinyin(s: String): String {
        var result = ""
        var temp = ""

        s.lowercase().forEach { c ->
            when {
                c in 'a'..'z' -> {
                    temp += c
                }

                c == ':' -> {
                    require(temp.lastOrNull() == 'u') { "Expected 'u' before ':'" }
                    temp = temp.substring(0, temp.length - 1) + "ü"
                }

                else -> {
                    if (c in '1'..'5') {  // Changed from '0'..'5' to '1'..'5'
                        val tone = c.digitToInt()
                        if (tone != 5) {  // Changed from tone != 0
                            val vowels = Regex("[aoeiuvü]+").find(temp)
                            when {
                                vowels == null -> {
                                    temp += c
                                }

                                vowels.value.length == 1 -> {
                                    val vowelIndex =
                                        pinyinToneMark[5]!!.indexOf(vowels.value)  // Changed from 0 to 5
                                    temp = temp.substring(0, vowels.range.first) +
                                            pinyinToneMark[tone]!![vowelIndex] +
                                            temp.substring(vowels.range.last + 1)
                                }

                                else -> {
                                    temp = when {
                                        'a' in temp -> temp.replace(
                                            "a",
                                            pinyinToneMark[tone]!![0].toString()
                                        )

                                        'o' in temp -> temp.replace(
                                            "o",
                                            pinyinToneMark[tone]!![1].toString()
                                        )

                                        'e' in temp -> temp.replace(
                                            "e",
                                            pinyinToneMark[tone]!![2].toString()
                                        )

                                        temp.endsWith("ui") -> temp.replace(
                                            "i",
                                            pinyinToneMark[tone]!![3].toString()
                                        )

                                        temp.endsWith("iu") -> temp.replace(
                                            "u",
                                            pinyinToneMark[tone]!![4].toString()
                                        )

                                        else -> temp + "!"
                                    }
                                }
                            }
                        }
                    }
                    result += temp
                    temp = ""
                }
            }
        }
        result += temp
        return result
    }

    fun numberedToAccented(pinyin: String): String {
        return pinyin.split(" ")
            .map { decodePinyin(it) }
            .joinToString(" ")
    }

}

// Add this to your repository to debug:

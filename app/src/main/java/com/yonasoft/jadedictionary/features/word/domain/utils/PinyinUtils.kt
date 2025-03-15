package com.yonasoft.jadedictionary.features.word.domain.utils

import net.sourceforge.pinyin4j.PinyinHelper

class PinyinUtils() {

    companion object {
        fun toPinyin(hanzi: String): String {
            return hanzi.map { char ->
                val pinyinArray = PinyinHelper.toHanyuPinyinStringArray(char)
                pinyinArray?.firstOrNull() ?: char.toString()
            }.joinToString(" ")
        }

        fun toPinyinWithTones(hanzi: String): String {
            return decodePinyin(hanzi.map { char ->
                val pinyinArray = PinyinHelper.toHanyuPinyinStringArray(char)
                pinyinArray?.firstOrNull() ?: char.toString()
            }.joinToString(" "))
        }
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

        private val initials = listOf(
            "b", "p", "m", "f", "d", "t", "n", "l",
            "g", "k", "h", "j", "q", "x", "zh", "ch",
            "sh", "r", "z", "c", "s", "y", "w"
        )

        // List of all Pinyin finals
        private val finals = listOf(
            "a", "o", "e", "ai", "ei", "ao", "ou", "an", "en", "ang", "eng",
            "i", "ia", "ie", "iao", "iu", "ian", "in", "iang", "ing", "iong",
            "u", "ua", "uo", "uai", "ui", "uan", "un", "uang", "ong",
            "ü", "üe", "üan", "ün"
        )


        // Regular expression to match a Pinyin syllable with a tone number
        private val pinyinRegex = Regex("(${initials.joinToString("|")})?(${finals.joinToString("|")})([1-5])?")

        // Existing accentToNumberMap and other methods...

        private fun splitIntoSyllables(pinyin: String): List<String> {
            val matches = pinyinRegex.findAll(pinyin)
            return matches.map { it.value }.toList()
        }


        fun normalizeQuery(input: String, markNeutral: Boolean = true): String {
            if (input.isBlank()) return ""

            // If it's Chinese characters, return as is
            if (input.any { it.code in 0x4E00..0x9FFF }) {
                return input.trim()
            }

            var normalized = input.trim().lowercase()

            // Check if the input contains accented characters
            val hasAccent = accentToNumberMap.keys.any { normalized.contains(it) }

            // If input has accent, convert to numbered pinyin
            if (hasAccent) {
                // Split into syllables if there are spaces
                val parts = if (normalized.contains(" ")) {
                    normalized.split(" ")
                } else {
                    // Use regex to split syllables
                    val syllableMatches = pinyinRegex.findAll(normalized)
                    syllableMatches.map { it.value }.toList()
                }

                // Convert each part
                val converted = parts.map { part ->
                    var syllable = part
                    var tone = ""

                    // Find the first accent in the syllable
                    accentToNumberMap.forEach { (accent, numbered) ->
                        if (syllable.contains(accent)) {
                            syllable = syllable.replace(accent, numbered[0].toString())
                            tone = numbered[1].toString()
                        }
                    }

                    "$syllable$tone"
                }

                // Join the converted syllables
                normalized = converted.joinToString(" ")
            }

            // Handle 'ü' to 'v' conversion
            normalized = normalized.replace("ü", "v")

            // Ensure spaces between syllables if not already present
            if (!normalized.contains(" ") && normalized.any { it in '1'..'5' }) {
                normalized = normalized.replace(Regex("([a-z])([1-5])(?=[a-z])"), "$1$2 ")
            }

            return normalized
        }

        fun decodePinyin(s: String): String {
            var result = ""
            var temp = ""

            s.lowercase().forEach { c ->
                when (c) {
                    in 'a'..'z' -> {
                        temp += c
                    }
                    ':' -> {
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

                                            else -> "$temp!"
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
    }
}

// Add this to your repository to debug:

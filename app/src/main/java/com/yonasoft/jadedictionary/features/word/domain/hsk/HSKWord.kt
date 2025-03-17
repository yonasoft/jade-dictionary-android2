package com.yonasoft.jadedictionary.features.word.domain.hsk

import com.yonasoft.jadedictionary.features.word.domain.Word
import com.yonasoft.jadedictionary.features.word.domain.utils.PinyinUtils

/**
 * Data class representing a word from the HSK vocabulary list
 */
data class HSKWord(
    // Using frequency as unique ID
    val id: Long,

    // Main properties
    val simplified: String,
    val traditional: String?,
    val radical: String?,
    val frequency: Int,

    // HSK levels
    val hskOldLevel: Int?,
    val hskNewLevel: Int?,

    // Pronunciation and meaning
    val pinyin: String?,
    val numericPinyin: String?,
    val definitions: List<String>,

    // Additional properties
    val classifiers: List<String>?,
    val partsOfSpeech: List<String>?
) : Word {

    val displayText: String
        get() = buildString {
            append(simplified)
            traditional?.let {
                if (it != simplified) append(" ($it)")
            }
        }

    val displayPinyin: String
        get() = buildString {
            pinyin?.let {
                append(PinyinUtils.decodePinyin(it))
            }
        }

    val displayDefinition: String
        get() = definitions.joinToString("; ")

    val displayClassifiers: String
        get() = classifiers?.joinToString(", ") ?: ""

    val displayPartsOfSpeech: String
        get() = partsOfSpeech?.joinToString(", ") ?: ""
}
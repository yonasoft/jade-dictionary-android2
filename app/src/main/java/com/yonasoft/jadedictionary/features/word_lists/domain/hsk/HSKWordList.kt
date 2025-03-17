package com.yonasoft.jadedictionary.features.word_lists.domain.hsk

import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKLevel
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKVersion
import com.yonasoft.jadedictionary.features.word_lists.domain.WordList

/**
 * Represents an HSK word list with metadata
 */
data class HSKWordList(
    override val id: Long,
    override val title: String,
    override val description: String,
    val version: HSKVersion,
    val level: HSKLevel,
    val wordCount: Int
) : WordList {
    // Properties to make it compatible with UI that expects CCWordList
    override val createdAt: Long = System.currentTimeMillis()
    override val updatedAt: Long = System.currentTimeMillis()
    override val numberOfWords: Long = wordCount.toLong()

    // HSK lists are not editable
    override val isEditable: Boolean = false
}
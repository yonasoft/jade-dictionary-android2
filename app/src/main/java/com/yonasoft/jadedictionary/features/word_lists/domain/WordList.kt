package com.yonasoft.jadedictionary.features.word_lists.domain

interface WordList {
    val id: Long?
    val title: String
    val description: String
    val createdAt: Long
    val updatedAt: Long
    val numberOfWords: Long
    val isEditable: Boolean
}
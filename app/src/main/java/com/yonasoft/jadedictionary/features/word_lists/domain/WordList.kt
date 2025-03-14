package com.yonasoft.jadedictionary.features.word_lists.domain

interface WordList {
    val title: String
    val description: String
    val wordIds: List<Long>
    val createdAt: Long
    val updatedAt: Long
    val numberOfWords:Long
}
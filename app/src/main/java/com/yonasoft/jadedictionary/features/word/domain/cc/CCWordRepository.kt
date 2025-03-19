package com.yonasoft.jadedictionary.features.word.domain.cc

interface CCWordRepository {
    suspend fun getWordById(id: Long): CCWord?
    suspend fun getWordByIds(ids: List<Long>): List<CCWord>
    suspend fun searchWords(query: String): List<CCWord>
    suspend fun getAllWords(): List<CCWord>
    suspend fun getCharsFromWord(word: String): List<CCWord>
    suspend fun getWordsFromWord(word: String): List<CCWord>
}
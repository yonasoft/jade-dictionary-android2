package com.yonasoft.jadedictionary.core.words.domain.cc

import com.yonasoft.jadedictionary.core.words.data.cc.CCWord

interface CCWordRepository {
    suspend fun getWordById(id: Int): CCWord?
    suspend fun searchWords(query: String): List<CCWord>
    suspend fun getAllWords(): List<CCWord>
}
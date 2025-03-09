package com.yonasoft.jadedictionary.core.words.data.cc

import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository

class CCWordRepositoryImpl(private val dao: CCWordDao) : CCWordRepository {
    override suspend fun getWordById(id: Int): CCWord? = dao.getWordById(id)
    override suspend fun searchWords(query: String): List<CCWord> = dao.searchWords(query)
    override suspend fun getAllWords(): List<CCWord> = dao.getAllWords()
}
package com.yonasoft.jadedictionary.core.words.data.cc

import android.util.Log
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.core.words.utils.PinyinUtils

class CCWordRepositoryImpl(private val dao: CCWordDao) : CCWordRepository {
    override suspend fun getWordById(id: Int): CCWord? = dao.getWordById(id)

    override suspend fun searchWords(query: String): List<CCWord> {

        if (query.isBlank()) return emptyList()

        // Normalize the query, handling pinyin variations
        val normalizedQuery = PinyinUtils.normalizeQuery(query)

        Log.d("Repository", "Original query: '$query'")
        Log.d("Repository", "Normalized query: '$normalizedQuery'")

        return dao.searchWords(normalizedQuery)
    }

    override suspend fun getAllWords(): List<CCWord> = dao.getAllWords()
}
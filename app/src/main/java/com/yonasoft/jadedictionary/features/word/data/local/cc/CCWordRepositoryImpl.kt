package com.yonasoft.jadedictionary.features.word.data.local.cc

import android.content.Context
import android.util.Log
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.features.word.domain.utils.PinyinUtils

class CCWordRepositoryImpl(private val dao: CCWordDao, private val context: Context) :
    CCWordRepository {
    override suspend fun getWordById(id: Long): CCWord? = dao.getWordById(id)
    override suspend fun getWordByIds(ids: List<Long>): List<CCWord> = dao.getWordsByIds(ids)

    override suspend fun searchWords(query: String): List<CCWord> {

        if (query.isBlank()) return emptyList()
        val normalizedQuery = PinyinUtils.normalizeQuery(query)

        return dao.searchWords(normalizedQuery)
    }

    override suspend fun getAllWords(): List<CCWord> = dao.getAllWords()
    override suspend fun getCharsFromWord(word: String): List<CCWord> {
        Log.i("CCWordRepositoryImpl", "Fetching characters for word: $word")
        val characters = dao.getCharsFromWord(word)
        Log.i("CCWordRepositoryImpl", "Characters found: ${characters.size}")
        return characters
    }

    override suspend fun getWordsFromWord(word: String): List<CCWord> {
        return dao.getWordsFromWord(word)
    }

}
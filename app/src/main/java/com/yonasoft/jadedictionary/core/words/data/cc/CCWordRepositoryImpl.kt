package com.yonasoft.jadedictionary.core.words.data.cc

import android.content.Context
import android.util.Log
import com.yonasoft.jadedictionary.core.words.data.sentences.Sentence
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.core.words.utils.PinyinUtils
import com.yonasoft.jadedictionary.core.words.utils.SentenceUtil

class CCWordRepositoryImpl(private val dao: CCWordDao, private val context: Context) :
    CCWordRepository {
    override suspend fun getWordById(id: Long): CCWord? = dao.getWordById(id)

    override suspend fun searchWords(query: String): List<CCWord> {

        if (query.isBlank()) return emptyList()

        // Normalize the query, handling pinyin variations
        val normalizedQuery = PinyinUtils.normalizeQuery(query)

        Log.d("Repository", "Original query: '$query'")
        Log.d("Repository", "Normalized query: '$normalizedQuery'")

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

    override suspend fun getSentencesFromWord(word: String): List<Sentence> {
        return SentenceUtil.searchDefaultSentences(context = context, searchString = word)
    }
}
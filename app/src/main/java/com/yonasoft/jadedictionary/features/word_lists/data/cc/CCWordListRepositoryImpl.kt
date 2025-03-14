package com.yonasoft.jadedictionary.features.word_lists.data.cc

import android.content.Context
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordListRepository

class CCWordListRepositoryImpl(
    private val wordListDao: CCWordListDao,
    private val context: Context
) : CCWordListRepository {

    override fun getAllWordLists(): List<CCWordList> {
        return wordListDao.getAllWordLists()
    }

    override suspend fun getWordListById(id: Long): CCWordList? {
        return wordListDao.getWordListById(id)
    }

    override fun searchWordLists(query: String): List<CCWordList> {
        return wordListDao.searchWordLists(query)
    }

    override suspend fun insertWordList(wordList: CCWordList): Long {
        return wordListDao.insertWordList(wordList)
    }

    override suspend fun updateWordList(wordList: CCWordList) {
        wordListDao.updateWordList(wordList)
    }

    override suspend fun deleteWordList(wordList: CCWordList) {
        wordListDao.deleteWordList(wordList)
    }

    override suspend fun deleteWordListById(id: Long) {
        wordListDao.deleteWordListById(id)
    }
}
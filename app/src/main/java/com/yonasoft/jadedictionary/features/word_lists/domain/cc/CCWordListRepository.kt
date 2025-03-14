package com.yonasoft.jadedictionary.features.word_lists.domain.cc

import kotlinx.coroutines.flow.Flow

interface CCWordListRepository {
    fun getAllWordLists(): Flow<List<CCWordList>>
    suspend fun getWordListById(id: Long): CCWordList?
    fun searchWordLists(query: String): Flow<List<CCWordList>>
    suspend fun insertWordList(wordList: CCWordList): Long
    suspend fun updateWordList(wordList: CCWordList)
    suspend fun deleteWordList(wordList: CCWordList)
    suspend fun deleteWordListById(id: Long)
}
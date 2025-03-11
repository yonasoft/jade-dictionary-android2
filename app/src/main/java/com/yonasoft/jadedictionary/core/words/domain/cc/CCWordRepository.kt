package com.yonasoft.jadedictionary.core.words.domain.cc

import com.yonasoft.jadedictionary.core.words.data.cc.CCWord

interface CCWordRepository {
    suspend fun getWordById(id: Long): CCWord?
    suspend fun searchWords(query: String): List<CCWord>
    suspend fun getAllWords(): List<CCWord>
    suspend fun getCharsFromWord(word:String):List<CCWord>
    suspend fun getWordsFromWord(word:String):List<CCWord>

}
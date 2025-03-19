package com.yonasoft.jadedictionary.features.word.domain.hsk

interface HSKWordRepository {

    suspend fun getAllWords(): List<HSKWord>
    suspend fun getWordById(id: Long): HSKWord?
    suspend fun getWordBySimplified(simplified: String): HSKWord?
    suspend fun getWordByTraditional(traditional: String): HSKWord?
    suspend fun getWordsByLevel(version: HSKVersion, level: HSKLevel): List<HSKWord>
    suspend fun getWordsByLevels(version: HSKVersion, levels: List<HSKLevel>): List<HSKWord>
    suspend fun getWordsByLevelRange(version: HSKVersion, minLevel: HSKLevel, maxLevel: HSKLevel): List<HSKWord>
    suspend fun reloadData()
}
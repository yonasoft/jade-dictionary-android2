package com.yonasoft.jadedictionary.features.word.data.local.hsk

import android.content.Context
import android.util.Log
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKLevel
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKVersion
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private val HSK_FILE_PATHS = listOf("words/hsk/complete.min.json", "words/hsk/7.min.json")

class HSKWordRepositoryImpl(
    private val context: Context
) : HSKWordRepository {

    companion object {
        private const val TAG = "HSKWordRepository"
    }

    private val mutex = Mutex()

    private var wordsCache: List<HSKWord>? = null


    private suspend fun getWordsCache(): List<HSKWord> {
        if (wordsCache == null) {
            mutex.withLock {
                if (wordsCache == null) {
                    val res = mutableListOf<HSKWord>()
                    HSK_FILE_PATHS.forEach {
                        res.addAll(HSKWordParser.parseHSKWords(context, it))
                        Log.i("file name", it)
                    }
                    wordsCache = res
                    Log.d(TAG, "Loaded ${wordsCache?.size ?: 0} HSK words into cache")
                }
            }
        }
        return wordsCache ?: emptyList()
    }

    override suspend fun getAllWords(): List<HSKWord> = getWordsCache()

    override suspend fun getWordById(id: Long): HSKWord? = withContext(Dispatchers.Default) {
        getWordsCache().find { it.id == id }
    }

    override suspend fun getWordBySimplified(simplified: String): HSKWord? =
        withContext(Dispatchers.Default) {
            getWordsCache().find { it.simplified == simplified }
        }

    override suspend fun getWordByTraditional(traditional: String): HSKWord? =
        withContext(Dispatchers.Default) {
            getWordsCache().find { it.traditional == traditional }
        }

    override suspend fun getWordsByLevel(version: HSKVersion, level: HSKLevel): List<HSKWord> =
        withContext(Dispatchers.Default) {
            getWordsCache().filter { word ->
                when (version) {
                    HSKVersion.OLD -> word.hskOldLevel == level.value
                    HSKVersion.NEW -> word.hskNewLevel == level.value
                }
            }
        }

    override suspend fun getWordsByLevels(
        version: HSKVersion,
        levels: List<HSKLevel>
    ): List<HSKWord> = withContext(Dispatchers.Default) {
        val levelValues = levels.map { it.value }
        getWordsCache().filter { word ->
            when (version) {
                HSKVersion.OLD -> word.hskOldLevel != null && word.hskOldLevel in levelValues
                HSKVersion.NEW -> word.hskNewLevel != null && word.hskNewLevel in levelValues
            }
        }
    }

    override suspend fun getWordsByLevelRange(
        version: HSKVersion,
        minLevel: HSKLevel,
        maxLevel: HSKLevel
    ): List<HSKWord> = withContext(Dispatchers.Default) {
        val levelRange = minLevel.value..maxLevel.value
        getWordsCache().filter { word ->
            when (version) {
                HSKVersion.OLD -> word.hskOldLevel != null && word.hskOldLevel in levelRange
                HSKVersion.NEW -> word.hskNewLevel != null && word.hskNewLevel in levelRange
            }
        }
    }

    override suspend fun reloadData() {
        mutex.withLock {
            wordsCache = null
            getWordsCache()
        }
    }
}
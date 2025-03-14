package com.yonasoft.jadedictionary.features.word_lists.data.cc

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList

@Dao
interface CCWordListDao {
    @Query("SELECT * FROM cc_wordlist")
    fun getAllWordLists(): List<CCWordList>

    @Query("SELECT * FROM cc_wordlist WHERE _id = :id")
    suspend fun getWordListById(id: Long): CCWordList?


    @Query("SELECT * FROM cc_wordlist WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY CASE WHEN title LIKE :query || '%' THEN 0 WHEN title LIKE '%' || :query || '%' THEN 1 WHEN description LIKE :query || '%' THEN 2 ELSE 3 END")
    fun searchWordLists(query: String): List<CCWordList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordList(wordList: CCWordList): Long

    @Update
    suspend fun updateWordList(wordList: CCWordList)

    @Delete
    suspend fun deleteWordList(wordList: CCWordList)

    @Query("DELETE FROM cc_wordlist WHERE _id = :id")
    suspend fun deleteWordListById(id: Long)
}
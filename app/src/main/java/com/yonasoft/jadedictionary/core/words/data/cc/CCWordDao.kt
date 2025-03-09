package com.yonasoft.jadedictionary.core.words.data.cc

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns

@Dao
interface CCWordDao {
    @Query("SELECT * FROM cc_words WHERE _id = :id")
    suspend fun getWordById(id: Int): CCWord?

    @RewriteQueriesToDropUnusedColumns
    @Query("""
    SELECT * FROM cc_words
    WHERE 
        simplified LIKE '%' || :query || '%'
        OR traditional LIKE '%' || :query || '%'
        OR pinyin LIKE '%' || :query || '%'
        OR pinyin LIKE '%' || REPLACE(:query, ' ', '') || '%'
        OR definition LIKE '%' || :query || '%'
    ORDER BY 
        CASE 
            WHEN simplified = :query THEN 300
            WHEN traditional = :query THEN 300
            WHEN pinyin = :query THEN 250
            WHEN pinyin = REPLACE(:query, ' ', '') THEN 250
            WHEN definition LIKE :query || ' %' OR definition = :query THEN 200
            WHEN simplified LIKE '%' || :query || '%' THEN 150
            WHEN traditional LIKE '%' || :query || '%' THEN 150
            WHEN pinyin LIKE '%' || :query || '%' THEN 100
            WHEN definition LIKE '%' || :query || '%' THEN 50
            ELSE 0
        END DESC,
        length(simplified) ASC,
        simplified ASC
    LIMIT 50
""")
    suspend fun searchWords(query: String): List<CCWord>

    @Query("SELECT * FROM cc_words")
    suspend fun getAllWords(): List<CCWord>
}

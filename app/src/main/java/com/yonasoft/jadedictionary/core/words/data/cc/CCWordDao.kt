package com.yonasoft.jadedictionary.core.words.data.cc

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CCWordDao {
    @Query("SELECT * FROM cc_words WHERE _id = :id")
    suspend fun getWordById(id: Int): CCWord?

    @Query("""
    SELECT *, 
        CASE 
            -- Exact word match at start of definition
            WHEN definition LIKE :query || ' %' 
                OR definition LIKE :query || ';%' 
                OR definition = :query THEN 100
                
            -- Main meaning contains exact word
            WHEN definition LIKE '% ' || :query || ' %'
                OR definition LIKE '% ' || :query || ';%'
                OR definition LIKE '% ' || :query || ',%' THEN 90
                
            -- Character matches
            WHEN simplified = :query OR traditional = :query THEN 85
            WHEN pinyin = :query THEN 80
                
            -- Starts with matches
            WHEN simplified LIKE :query || '%' 
                OR traditional LIKE :query || '%' THEN 70
            WHEN pinyin LIKE :query || '%' THEN 65
                
            -- Definition contains word as part of another word
            WHEN definition LIKE '%' || :query || '%' THEN 60
                
            -- Contains matches
            WHEN simplified LIKE '%' || :query || '%' 
                OR traditional LIKE '%' || :query || '%' THEN 50
            WHEN pinyin LIKE '%' || :query || '%' THEN 40
                
            ELSE 0
        END as ranking,
        -- Additional ranking for definition position
        CASE 
            WHEN definition LIKE :query || ' %' THEN 0
            WHEN definition LIKE '% ' || :query || ' %' THEN 
                LENGTH(SUBSTR(definition, 1, INSTR(definition, :query)))
            ELSE 999
        END as defPosition,
        -- Penalty for parenthetical definitions
        CASE 
            WHEN definition LIKE '(%' THEN 1
            ELSE 0
        END as isParenthetical,
        length(simplified) as wordLength
    FROM cc_words
    WHERE 
        simplified = :query 
        OR traditional = :query
        OR pinyin = :query
        OR simplified LIKE '%' || :query || '%'
        OR traditional LIKE '%' || :query || '%'
        OR pinyin LIKE '%' || :query || '%'
        OR definition LIKE '%' || :query || '%'
    ORDER BY 
        ranking DESC,
        isParenthetical ASC,
        defPosition ASC,
        wordLength ASC,
        simplified ASC
    LIMIT 50
""")
    suspend fun searchWords(query: String): List<CCWord>

    @Query("SELECT * FROM cc_words")
    suspend fun getAllWords(): List<CCWord>
}

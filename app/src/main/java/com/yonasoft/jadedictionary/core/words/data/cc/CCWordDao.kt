package com.yonasoft.jadedictionary.core.words.data.cc

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns

@Dao
interface CCWordDao {
    @Query("SELECT * FROM cc_words WHERE _id = :id")
    suspend fun getWordById(id: Long): CCWord?

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
WITH RECURSIVE
  input_split(syllable, rest) AS (
    SELECT '', :query || ' '
    UNION ALL
    SELECT
      substr(rest, 0, instr(rest, ' ')),
      substr(rest, instr(rest, ' ') + 1)
    FROM input_split
    WHERE rest <> ''
  ),
  syllable_count AS (
    SELECT COUNT(*) as total_syllables 
    FROM input_split 
    WHERE syllable <> ''
  )

SELECT 
    *,
    CASE 
        -- Existing matches remain at higher priority
        WHEN simplified = :query THEN 300
        WHEN traditional = :query THEN 300
        WHEN definition = :query THEN 250
        WHEN pinyin = :query THEN 250
        WHEN simplified LIKE '%' || :query || '%' THEN 200
        WHEN traditional LIKE '%' || :query || '%' THEN 200
        WHEN definition LIKE '%' || :query || '%' THEN 150
        WHEN pinyin LIKE '%' || :query || '%' THEN 125
        
        -- All syllables in exact order
        WHEN pinyin = (
            SELECT group_concat(syllable, ' ') 
            FROM input_split 
            WHERE syllable <> ''
        ) THEN 200
        
        -- All syllables in any order
        WHEN (
            SELECT COUNT(*) 
            FROM input_split 
            WHERE 
                syllable <> '' AND 
                pinyin LIKE '%' || syllable || '%'
        ) = (SELECT total_syllables FROM syllable_count) THEN 175
        
        -- Containing all syllables (concatenated)
        WHEN pinyin LIKE '%' || (
            SELECT group_concat(syllable, '') 
            FROM input_split 
            WHERE syllable <> ''
        ) || '%' THEN 150
        
        -- Partial syllable matches (more syllables matched is better)
        WHEN (
            SELECT COUNT(*) 
            FROM input_split 
            WHERE 
                syllable <> '' AND 
                pinyin LIKE '%' || syllable || '%'
        ) > 1 THEN 100
        
        -- Single syllable matches
        WHEN (
            SELECT COUNT(*) 
            FROM input_split 
            WHERE 
                syllable <> '' AND 
                pinyin LIKE '%' || syllable || '%'
        ) > 0 THEN 50
        
        ELSE 0
    END as ranking
FROM cc_words, syllable_count
WHERE 
    simplified LIKE '%' || :query || '%'
    OR traditional LIKE '%' || :query || '%'
    OR definition LIKE '%' || :query || '%'
    OR pinyin LIKE '%' || :query || '%'
    OR pinyin LIKE '%' || REPLACE(:query, ' ', '') || '%'
    OR EXISTS (
        SELECT 1 
        FROM input_split 
        WHERE 
            syllable <> '' AND (
            simplified LIKE '%' || syllable || '%' OR
            traditional LIKE '%' || syllable || '%' OR
            definition LIKE '%' || syllable || '%' OR
            pinyin LIKE '%' || syllable || '%')
    )
ORDER BY 
    ranking DESC,
    length(simplified) ASC,
    simplified ASC
LIMIT 50
"""
    )
    suspend fun searchWords(query: String): List<CCWord>

    @Query(
        """
WITH RECURSIVE
  chars(char, idx) AS (
    SELECT substr(:word, 1, 1), 1
    UNION ALL
    SELECT substr(:word, idx + 1, 1), idx + 1
    FROM chars
    WHERE idx < length(:word)
  )

SELECT *
FROM cc_words
WHERE simplified IN (
    SELECT DISTINCT char 
    FROM chars 
    WHERE char <> ''
)
ORDER BY length(simplified) ASC
LIMIT 50
"""
    )
    suspend fun getCharsFromWord(word: String): List<CCWord>

    @Query(
        """
SELECT * FROM cc_words 
WHERE simplified LIKE '%' || :word|| '%'
ORDER BY 
    length(simplified) ASC,
    simplified ASC
LIMIT 50
"""
    )
    suspend fun getWordsFromWord(word: String): List<CCWord>

    @Query("SELECT * FROM cc_words")
    suspend fun getAllWords(): List<CCWord>
}


package com.yonasoft.jadedictionary.features.word.data.local.cc

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord

@Dao
interface CCWordDao {
    @Query("SELECT * FROM cc_words WHERE _id = :id")
    suspend fun getWordById(id: Long): CCWord?

    @Query("SELECT * FROM cc_words WHERE _id IN (:ids)")
    suspend fun getWordsByIds(ids: List<Long>): List<CCWord>

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
        -- Exact matches (highest priority)
        WHEN simplified = :query THEN 1000
        WHEN traditional = :query THEN 1000
        WHEN pinyin = :query THEN 900
        WHEN definition = :query THEN 800
        
        -- Starts with matches (high priority)
        WHEN simplified LIKE :query || '%' THEN 700
        WHEN traditional LIKE :query || '%' THEN 700
        WHEN pinyin LIKE :query || '%' THEN 600
        WHEN definition LIKE :query || '%' THEN 500
        
        -- Contains matches (medium priority)
        WHEN simplified LIKE '%' || :query || '%' THEN 400
        WHEN traditional LIKE '%' || :query || '%' THEN 400
        WHEN pinyin LIKE '%' || :query || '%' THEN 350
        WHEN definition LIKE '%' || :query || '%' THEN 300
        
        -- All syllables in exact order
        WHEN pinyin = (
            SELECT group_concat(syllable, ' ') 
            FROM input_split 
            WHERE syllable <> ''
        ) THEN 400
        
        -- All syllables in any order
        WHEN (
            SELECT COUNT(*) 
            FROM input_split 
            WHERE 
                syllable <> '' AND 
                pinyin LIKE '%' || syllable || '%'
        ) = (SELECT total_syllables FROM syllable_count) THEN 350
        
        -- Word starts with syllable(s)
        WHEN pinyin LIKE (
            SELECT group_concat(syllable, ' ') 
            FROM input_split 
            WHERE syllable <> ''
        ) || '%' THEN 320
        
        -- Containing all syllables (concatenated)
        WHEN pinyin LIKE '%' || (
            SELECT group_concat(syllable, '') 
            FROM input_split 
            WHERE syllable <> ''
        ) || '%' THEN 250
        
        -- Partial syllable matches (more syllables matched is better)
        WHEN (
            SELECT COUNT(*) 
            FROM input_split 
            WHERE 
                syllable <> '' AND 
                pinyin LIKE '%' || syllable || '%'
        ) > 1 THEN 200
        
        -- Single syllable matches
        WHEN (
            SELECT COUNT(*) 
            FROM input_split 
            WHERE 
                syllable <> '' AND 
                pinyin LIKE '%' || syllable || '%'
        ) > 0 THEN 100
        
        -- Word starts with the first character of query
        WHEN simplified LIKE substr(:query, 1, 1) || '%' THEN 90
        WHEN traditional LIKE substr(:query, 1, 1) || '%' THEN 90
        
        -- Last resort matches
        WHEN simplified LIKE '%' || substr(:query, 1, 1) || '%' THEN 50
        WHEN traditional LIKE '%' || substr(:query, 1, 1) || '%' THEN 50
        
        ELSE 0
    END as ranking,
    CASE
        -- Prioritize shorter words with same rank
        WHEN length(simplified) = 1 THEN 10
        WHEN length(simplified) = 2 THEN 8
        WHEN length(simplified) = 3 THEN 6
        WHEN length(simplified) <= 5 THEN 4
        ELSE 2
    END as length_priority
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
    OR simplified LIKE substr(:query, 1, 1) || '%'
    OR traditional LIKE substr(:query, 1, 1) || '%'
ORDER BY 
    ranking DESC,
    length_priority DESC,
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
    CASE
        WHEN simplified = :word THEN 3
        WHEN simplified LIKE :word || '%' THEN 2
        ELSE 1
    END DESC,
    length(simplified) ASC,
    simplified ASC
LIMIT 50
"""
    )
    suspend fun getWordsFromWord(word: String): List<CCWord>

    @Query("SELECT * FROM cc_words")
    suspend fun getAllWords(): List<CCWord>
}
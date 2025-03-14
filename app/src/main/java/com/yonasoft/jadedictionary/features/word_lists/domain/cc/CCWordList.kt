package com.yonasoft.jadedictionary.features.word_lists.domain.cc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yonasoft.jadedictionary.features.word_lists.domain.WordList

@Entity(
    tableName = "cc_wordlist",
)
data class CCWordList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long? = null,

    @ColumnInfo(name = "title")
    override val title: String = "",

    @ColumnInfo(name = "description")
    override val description: String = "",

    @ColumnInfo(name = "wordIds")
    override val wordIds: List<Long> = emptyList(),

    @ColumnInfo(name = "created_at")
    override val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    override val updatedAt: Long,

    @ColumnInfo(name = "number_of_words")
    override val numberOfWords: Long,
) : WordList





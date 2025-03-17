package com.yonasoft.jadedictionary.features.word_lists.domain.cc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.yonasoft.jadedictionary.features.word_lists.domain.WordList

@Entity(tableName = "word_lists")
data class CCWordList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    override val id: Long? = null,

    @ColumnInfo(name = "title")
    override val title: String,

    @ColumnInfo(name = "description")
    override val description: String = "",

    @ColumnInfo(name = "created_at")
    override val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    override val updatedAt: Long,

    @ColumnInfo(name = "number_of_words")
    override val numberOfWords: Long = 0,

    @ColumnInfo(name = "word_ids")
    val wordIds: List<Long> = emptyList()
) : WordList {
    // Add @Ignore annotation to tell Room to ignore this property
    @Ignore
    override val isEditable: Boolean = true
}
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
    val id: Long,

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "wordIds")
    val wordIds: List<Long> = emptyList(),

    ) : WordList {

}



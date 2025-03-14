package com.yonasoft.jadedictionary.core.words.domain.cc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.yonasoft.jadedictionary.core.words.domain.Word
import com.yonasoft.jadedictionary.core.words.domain.utils.PinyinUtils

@Entity(
    tableName = "cc_words",
    indices = [
        Index(value = ["simplified"]),
        Index(value = ["traditional"]),
        Index(value = ["pinyin"])
    ]
)
data class CCWord(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id")
    val id: Long? = null,

    @ColumnInfo(name = "simplified")
    val simplified: String? = null,

    @ColumnInfo(name = "traditional")
    val traditional: String? = null,

    @ColumnInfo(name = "pinyin")
    val pinyin: String? = null,

    @ColumnInfo(name = "definition")
    val definition: String? = null
) : Word {
    val displayText: String
        get() = buildString {
            simplified?.let { append(it) }
            traditional?.let {
                if (it != simplified) append(" ($it)")
            }
        }

    val displayPinyin:String
        get() = buildString {
        pinyin?.let {
            append(" - ${PinyinUtils.decodePinyin(it)}")
        }
    }
}



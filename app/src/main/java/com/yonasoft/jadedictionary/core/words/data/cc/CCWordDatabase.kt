package com.yonasoft.jadedictionary.core.words.data.cc

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWord

@Database(entities = [CCWord::class], version = 1)
abstract class CCWordDatabase : RoomDatabase() {
    abstract fun ccWordDao(): CCWordDao

    companion object {
        fun getDatabase(context: Context): CCWordDatabase {
            return Room.databaseBuilder(context, CCWordDatabase::class.java, "words.db")
                .createFromAsset("words/cc/words.db")
                .fallbackToDestructiveMigration(false)
                .build()
        }
    }
}

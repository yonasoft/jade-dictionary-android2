package com.yonasoft.jadedictionary.features.word_lists.data.cc

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordList
import com.yonasoft.jadedictionary.features.word_lists.domain.utils.Converters


@Database(
    entities = [CCWordList::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CCWordListDatabase : RoomDatabase() {
    abstract fun ccWordListDao(): CCWordListDao

    companion object {
        @Volatile
        private var INSTANCE: CCWordListDatabase? = null

        fun getDatabase(context: Context): CCWordListDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CCWordListDatabase::class.java,
                    "cc_wordlist_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
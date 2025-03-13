// File: AppModule.kt
package com.yonasoft.jadedictionary.di

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.yonasoft.jadedictionary.core.words.data.cc.CCWordDatabase
import com.yonasoft.jadedictionary.core.words.data.cc.CCWordRepositoryImpl
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.core.words.utils.PinyinUtils
import com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels.WordDetailViewModel
import com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels.WordSearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    // Provide the Room database instance
    single {
        Log.i("search", "word db init complete")
        CCWordDatabase.getDatabase(androidContext()).also {
            Log.i("search", "word db init complete")
        }
    }
    single { get<CCWordDatabase>().ccWordDao() }
    single { PinyinUtils() }
    single<CCWordRepository> { CCWordRepositoryImpl(get(), androidContext()) }
    single { WordSearchViewModel(application = get(), repository = get()) }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        WordDetailViewModel(get(), savedStateHandle)
    }
}

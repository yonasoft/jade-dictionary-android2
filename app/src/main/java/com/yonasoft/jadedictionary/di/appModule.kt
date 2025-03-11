// File: AppModule.kt
package com.yonasoft.jadedictionary.di

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.yonasoft.jadedictionary.core.words.data.cc.CCWordDatabase
import com.yonasoft.jadedictionary.core.words.data.cc.CCWordRepositoryImpl
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository
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

    // Provide the DAO
    single { get<CCWordDatabase>().ccWordDao() }

    // Provide the repository implementation
    single<CCWordRepository> { CCWordRepositoryImpl(get()) }

    // Provide the ViewModel
    viewModel { WordSearchViewModel(get()) }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        WordDetailViewModel(get(), savedStateHandle)
    }
}

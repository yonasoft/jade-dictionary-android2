// File: AppModule.kt
package com.yonasoft.jadedictionary.di

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.yonasoft.jadedictionary.core.words.data.cc.CCWordDatabase
import com.yonasoft.jadedictionary.core.words.data.cc.CCWordRepositoryImpl
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.core.words.domain.utils.PinyinUtils
import com.yonasoft.jadedictionary.features.word_lists.data.cc.CCWordListDatabase
import com.yonasoft.jadedictionary.features.word_lists.data.cc.CCWordListRepositoryImpl
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordListRepository
import com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels.WordListsViewModel
import com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels.WordDetailViewModel
import com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels.WordSearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    // Provide the Room database instances
    single {
        Log.i("search", "word db init complete")
        CCWordDatabase.getDatabase(androidContext()).also {
            Log.i("search", "word db init complete")
        }
    }

    // CCWordList database and dependencies
    single {
        CCWordListDatabase.getDatabase(androidContext()).also {
            Log.i("wordlist", "wordlist db init complete")
        }
    }

    // DAOs
    single { get<CCWordDatabase>().ccWordDao() }
    single { get<CCWordListDatabase>().ccWordListDao() }

    // Utils
    single { PinyinUtils() }

    // Repositories
    single<CCWordRepository> { CCWordRepositoryImpl(get(), androidContext()) }
    single<CCWordListRepository> { CCWordListRepositoryImpl(get(), androidContext()) }

    // ViewModels
    single { WordSearchViewModel(application = get(), repository = get()) }
    single { WordListsViewModel(get()) }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        WordDetailViewModel(get(), savedStateHandle)
    }
}

package com.yonasoft.jadedictionary.di

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.yonasoft.jadedictionary.core.stores.settings.ThemePreferences
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.CCPracticeSetupViewModel
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.FlashCardPracticeViewModel
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.HSKPracticeSetupViewModel
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.ListeningPracticeViewModel
import com.yonasoft.jadedictionary.features.practice.presentation.viewmodels.MultipleChoicePracticeViewModel
import com.yonasoft.jadedictionary.features.settings.presentation.viewmodels.SettingsViewModel
import com.yonasoft.jadedictionary.features.word.data.local.cc.CCWordDatabase
import com.yonasoft.jadedictionary.features.word.data.local.cc.CCWordRepositoryImpl
import com.yonasoft.jadedictionary.features.word.data.local.hsk.HSKWordRepositoryImpl
import com.yonasoft.jadedictionary.features.word.data.local.sentences.SentenceRepositoryImpl
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWordRepository
import com.yonasoft.jadedictionary.features.word.domain.sentences.SentenceRespository
import com.yonasoft.jadedictionary.features.word.domain.utils.PinyinUtils
import com.yonasoft.jadedictionary.features.word.presentation.viewmodels.CCWordDetailViewModel
import com.yonasoft.jadedictionary.features.word.presentation.viewmodels.HSKWordDetailViewModel
import com.yonasoft.jadedictionary.features.word_lists.data.cc.CCWordListDatabase
import com.yonasoft.jadedictionary.features.word_lists.data.cc.CCWordListRepositoryImpl
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordListRepository
import com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels.WordListDetailViewModel
import com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels.WordListsViewModel
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
    single<SentenceRespository> { SentenceRepositoryImpl(androidContext()) }
    single<CCWordListRepository> { CCWordListRepositoryImpl(get(), androidContext()) }
    single<HSKWordRepository> {
        HSKWordRepositoryImpl(androidContext()).also {
            Log.i("HSK", "HSK repository initialized")
        }
    }

    single { ThemePreferences(androidContext()) }

    // ViewModels
    viewModel { WordSearchViewModel(application = get(), get(), get()) }
    viewModel { WordListsViewModel(get(), get()) }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        CCWordDetailViewModel(get(), get(), get(), savedStateHandle) // Pass both repositories
    }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        HSKWordDetailViewModel(get(), get(), get(), savedStateHandle)
    }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        WordListDetailViewModel(get(), get(), get(), (savedStateHandle))
    }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        CCPracticeSetupViewModel(get(), get(), (savedStateHandle))
    }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        HSKPracticeSetupViewModel(get(), (savedStateHandle))
    }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        FlashCardPracticeViewModel(get(), get(), (savedStateHandle))
    }
    viewModel { parameters ->
        MultipleChoicePracticeViewModel(
            ccWordRepository = get(),
            hskWordRepository = get(),
            savedStateHandle = parameters.get()
        )
    }

    viewModel { parameters ->
        ListeningPracticeViewModel(
            ccWordRepository = get(),
            hskWordRepository = get(),
            savedStateHandle = parameters.get()
        )
    }

    viewModel {
        SettingsViewModel(get())
    }
}
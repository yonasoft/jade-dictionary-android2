package com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository

class WordDetailViewModel(private val repository: CCWordRepository) : ViewModel()
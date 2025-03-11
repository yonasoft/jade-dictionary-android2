package com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.core.words.data.cc.CCWord
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordDetailViewModel(
    private val repository: CCWordRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _wordId = MutableStateFlow<Long?>(null)
    private val wordId: StateFlow<Long?> = _wordId.asStateFlow()

    private val _wordDetails = MutableStateFlow<CCWord?>(null)
    val wordDetails: StateFlow<CCWord?> = _wordDetails.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Main) {
            _wordId.value = savedStateHandle.get<Long>("wordId")

            wordId.collectLatest { id ->
                id?.let {
                    Log.i("word detail", "id: $id")
                    getWordDetails(it)
                }
            }
        }
    }

    private suspend fun getWordDetails(wordId: Long) {
        withContext(Dispatchers.IO) {
            try {
                _wordDetails.value = repository.getWordById(wordId)
            } catch (e: Exception) {
                Log.e("word detail", e.message.toString())
            }
        }
    }
}
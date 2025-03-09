package com.yonasoft.jadedictionary.features.word_search.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.core.words.data.cc.CCWord
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SharedWordViewModel(private val repository: CCWordRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery :StateFlow<String> = _searchQuery

    private val _words = MutableStateFlow<List<CCWord>>(emptyList())
    val words: StateFlow<List<CCWord>> = _words


    fun updateSearchQuery(newValue: String) {
        _searchQuery.value = newValue
    }

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Add a small delay to avoid too many searches while typing
                delay(300)
                Log.d("ViewModel", "Searching for: $query")
                val result = repository.searchWords(query)
                _words.value = result
                Log.d("ViewModel", "Found ${result.size} results")
            } catch (e: Exception) {
                Log.e("ViewModel", "Search failed", e)
            }
        }
    }
}

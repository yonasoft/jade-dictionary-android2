package com.yonasoft.jadedictionary.features.word_lists.presentation.viewmodels

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.ViewModel
import com.yonasoft.jadedictionary.core.words.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word_lists.domain.cc.CCWordListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WordListsViewModel(private val repository: CCWordListRepository):ViewModel() {


    private val _searchQuery = MutableStateFlow("")
    private val _words = MutableStateFlow<List<CCWord>>(emptyList())
    private val _selectedTab = MutableStateFlow(0)

    val searchQuery: StateFlow<String> = _searchQuery
    val selectedTab: StateFlow<Int> = _selectedTab

    val focusRequester = FocusRequester()
    val localFocusManager = LocalFocusManager
    val localKeyboardController = LocalSoftwareKeyboardController


    fun updateSearchQuery(newValue: String) {
        _searchQuery.value = newValue
    }

    fun updateInputTab(index: Int) {
        _selectedTab.value = index
    }

}
package com.yonasoft.jadedictionary.features.word.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWord
import com.yonasoft.jadedictionary.features.word.domain.cc.CCWordRepository
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWordRepository
import com.yonasoft.jadedictionary.features.word.domain.sentences.Sentence
import com.yonasoft.jadedictionary.features.word.domain.sentences.SentenceRespository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HSKWordDetailViewModel(
    private val ccWordRepository: CCWordRepository,
    private val hskRespository:HSKWordRepository,
    private val sentenceRespository: SentenceRespository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _wordId = MutableStateFlow<Long?>(null)
    private val _wordDetails = MutableStateFlow<HSKWord?>(null)
    private val _characters = MutableStateFlow<List<CCWord>>(emptyList())
    private val _wordsOfWord = MutableStateFlow<List<CCWord>>(emptyList())
    private val _sentences = MutableStateFlow<List<Sentence>>(emptyList())
    private val _selectedTab = MutableStateFlow(0)
    private val _isSpeaking = MutableStateFlow(false)

    val wordDetails: StateFlow<HSKWord?> = _wordDetails.asStateFlow()
    val characters: StateFlow<List<CCWord>> = _characters.asStateFlow()
    val wordsOfWord: StateFlow<List<CCWord>> = _wordsOfWord.asStateFlow()
    val sentences: StateFlow<List<Sentence>> = _sentences.asStateFlow()
    val tabIndex: StateFlow<Int> = _selectedTab.asStateFlow()
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val id = savedStateHandle.get<Long>("wordId")
            _wordId.value = id

            _wordId
                .filterNotNull()
                .collect { wordId ->
                    fetchWordDetails(wordId)
                }
        }

        viewModelScope.launch(Dispatchers.Unconfined) {
            _wordDetails
                .filterNotNull()
                .collectLatest { word ->
                    word.simplified?.let {
                        fetchCharacters(it)
                        fetchWordsOfWord(it)
                        fetchSentencesOfWord(it)
                    }
                }
        }

    }

    fun updateSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun setIsSpeaking(boolean: Boolean) {
        _isSpeaking.value = boolean
    }

    private suspend fun fetchWordDetails(wordId: Long) {
        withContext(Dispatchers.IO) {
            try {
                val wordDetails = hskRespository.getWordById(wordId)
                withContext(Dispatchers.Main) {
                    _wordDetails.value = wordDetails
                }
            } catch (e: Exception) {
                Log.e("WordDetailViewModel", "Error fetching word details", e)
            }
        }
    }

    private suspend fun fetchCharacters(characters: String) {
        withContext(Dispatchers.IO) {
            try {
                val fetchedCharacters = ccWordRepository.getCharsFromWord(characters)
                withContext(Dispatchers.Main) {
                    _characters.value = fetchedCharacters
                }
            } catch (e: Exception) {
                Log.e("WordDetailViewModel", "Error fetching characters", e)
            }
        }
    }

    private suspend fun fetchWordsOfWord(word: String) {
        withContext(Dispatchers.IO) {
            try {
                val fetchedWord = ccWordRepository.getWordsFromWord(word)
                withContext(Dispatchers.Main) {
                    _wordsOfWord.value = fetchedWord
                }
            } catch (e: Exception) {
                Log.e("WordDetailViewModel", "Error fetching words of word", e)
            }
        }
    }

    private suspend fun fetchSentencesOfWord(word: String) {
        withContext(Dispatchers.IO) {
            try {
                val fetchedSentences = sentenceRespository.getSentencesFromWord(word)
                Log.i("WordDetailViewModel", fetchedSentences.toString())
                withContext(Dispatchers.Main) {
                    _sentences.value = fetchedSentences
                }
            } catch (e: Exception) {
                Log.e("WordDetailViewModel", "Error fetching words of word", e)
            }
        }
    }
}
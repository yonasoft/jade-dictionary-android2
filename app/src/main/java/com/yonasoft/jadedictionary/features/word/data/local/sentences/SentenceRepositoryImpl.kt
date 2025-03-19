package com.yonasoft.jadedictionary.features.word.data.local.sentences

import android.content.Context
import com.yonasoft.jadedictionary.features.word.domain.sentences.Sentence
import com.yonasoft.jadedictionary.features.word.domain.sentences.SentenceRespository
import com.yonasoft.jadedictionary.features.word.domain.utils.SentenceUtil

class SentenceRepositoryImpl(private val context: Context):SentenceRespository {
    override suspend fun getSentencesFromWord(word: String): List<Sentence> {
        return SentenceUtil.searchDefaultSentences(context = context, searchString = word)
    }
}
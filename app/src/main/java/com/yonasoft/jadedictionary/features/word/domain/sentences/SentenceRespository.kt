package com.yonasoft.jadedictionary.features.word.domain.sentences

interface SentenceRespository {
    suspend fun getSentencesFromWord(word: String): List<Sentence>
}
package com.yonasoft.jadedictionary.features.word.domain.sentences


data class Sentence(
    val id: String,
    val chineseSentence: String,
    val tatoebaSentenceId: String,
    val englishTranslation: String
)

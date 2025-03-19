package com.yonasoft.jadedictionary.features.word.domain.utils

import android.content.Context
import com.yonasoft.jadedictionary.features.word.domain.sentences.Sentence
import java.io.BufferedReader
import java.io.InputStreamReader

object SentenceUtil {

    /**
     * Search for sentences containing the given string in a TSV file
     *
     * @param context The application context
     * @param searchString The string to search for in sentences
     * @param tsvFilePath The path to the TSV file in assets (e.g., "sentences/cmn_sentences.tsv")
     * @param maxResults The maximum number of results to return (default 15)
     * @return List of Sentence objects
     */
    fun searchSentences(
        context: Context,
        searchString: String,
        tsvFilePath: String = "sentences/sentences.tsv",
        maxResults: Int = 15
    ): List<Sentence> {
        val results = mutableListOf<Sentence>()

        try {
            context.assets.open(tsvFilePath).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String?
                    var resultCount = 0

                    while (reader.readLine().also { line = it } != null && resultCount < maxResults) {
                        line?.let {
                            val parts = it.split("\t")
                            if (parts.size >= 4) {
                                val id = parts[0]
                                val chineseSentence = parts[1]
                                val tatoebaSentenceId = parts[2]
                                val englishTranslation = parts[3]

                                if (chineseSentence.contains(searchString, ignoreCase = true)) {
                                    results.add(
                                        Sentence(
                                            id = id,
                                            chineseSentence = chineseSentence,
                                            tatoebaSentenceId = tatoebaSentenceId,
                                            englishTranslation = englishTranslation
                                        )
                                    )
                                    resultCount++
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return results
    }

    /**
     * Search for sentences in the default TSV file
     * located at app/src/main/assets/sentences/cmn_sentences.tsv
     *
     * @param context The application context
     * @param searchString The string to search for in sentences
     * @param maxResults The maximum number of results to return (default 15)
     * @return List of Sentence objects
     */
    fun searchDefaultSentences(
        context: Context,
        searchString: String,
        maxResults: Int = 15
    ): List<Sentence> {
        return searchSentences(context, searchString, "sentences/sentences.tsv", maxResults)
    }
}
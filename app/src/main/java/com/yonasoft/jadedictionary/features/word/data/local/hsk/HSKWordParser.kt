package com.yonasoft.jadedictionary.features.word.data.local.hsk

import android.content.Context
import android.util.Log
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKLevel
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKVersion
import com.yonasoft.jadedictionary.features.word.domain.hsk.HSKWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader


object HSKWordParser {
    private const val TAG = "HSKWordParser"

    suspend fun parseHSKWords(context: Context, path: String): List<HSKWord> = withContext(Dispatchers.IO) {
        val words = mutableListOf<HSKWord>()

        try {
            val jsonString = context.assets.open(path).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            }

            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                try {
                    val wordObject = jsonArray.getJSONObject(i)
                    parseWordFromJson(wordObject, path)?.let { words.add(it) }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing word at index $i: ${e.message}")
                }
            }

            Log.d(TAG, "Parsed ${words.size} HSK words successfully from $path")

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing HSK words from $path: ${e.message}")
            e.printStackTrace()
        }

        return@withContext words
    }

    private fun parseWordFromJson(json: JSONObject, path: String? = null): HSKWord? {
        try {
            // Parse the abbreviated fields according to the schema
            val simplified = json.optString("s", "")
            if (simplified.isEmpty()) return null

            val radical = json.optString("r", null)
            val frequency = json.optInt("q", 0)

            // Parse HSK levels
            var hskOldLevel: Int? = null
            var hskNewLevel: Int? = null

            val levelArray = json.optJSONArray("l")
            if (levelArray != null) {
                for (j in 0 until levelArray.length()) {
                    val levelTag = levelArray.getString(j)
                    val levelInfo = HSKLevel.fromJsonTag(levelTag)

                    if (levelInfo != null) {
                        val (version, level) = levelInfo
                        if (version == HSKVersion.OLD) {
                            hskOldLevel = level.value
                        } else {
                            hskNewLevel = level.value
                        }
                    }
                }
            }

            // Special handling for 7.min.json to always set new level to 7
            if (path?.contains("7.min.json") == true) {
                hskNewLevel = 7
            }

            // Parse parts of speech
            val partsOfSpeech = mutableListOf<String>()
            val posArray = json.optJSONArray("p")
            if (posArray != null) {
                for (j in 0 until posArray.length()) {
                    partsOfSpeech.add(posArray.getString(j))
                }
            }

            // Parse forms (there might be multiple, but we'll use the first one)
            val formsArray = json.optJSONArray("f")
            if (formsArray == null || formsArray.length() == 0) return null

            val form = formsArray.getJSONObject(0)
            val traditional = form.optString("t", null)

            // Parse transcriptions
            val transcriptions = form.optJSONObject("i")
            val pinyin = transcriptions?.optString("y", null)
            val numericPinyin = transcriptions?.optString("n", null)

            // Parse meanings
            val definitions = mutableListOf<String>()
            val meaningsArray = form.optJSONArray("m")
            if (meaningsArray != null) {
                for (j in 0 until meaningsArray.length()) {
                    definitions.add(meaningsArray.getString(j))
                }
            }

            // Parse classifiers
            val classifiers = mutableListOf<String>()
            val classifiersArray = form.optJSONArray("c")
            if (classifiersArray != null) {
                for (j in 0 until classifiersArray.length()) {
                    classifiers.add(classifiersArray.getString(j))
                }
            }

            return HSKWord(
                id = frequency.toLong(),
                simplified = simplified,
                traditional = traditional,
                radical = radical,
                frequency = frequency,
                hskOldLevel = hskOldLevel,
                hskNewLevel = hskNewLevel,
                pinyin = pinyin,
                numericPinyin = numericPinyin,
                definitions = definitions,
                classifiers = if (classifiers.isEmpty()) null else classifiers,
                partsOfSpeech = if (partsOfSpeech.isEmpty()) null else partsOfSpeech
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing word: ${e.message}")
            return null
        }
    }
}
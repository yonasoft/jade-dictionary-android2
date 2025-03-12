package com.yonasoft.jadedictionary.features.handwriting.domain.services

import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class HandwritingRecognizer {
    private val modelManager = RemoteModelManager.getInstance()

    // Define language identifiers with null safety
    private val zhModelIdentifier by lazy {
        DigitalInkRecognitionModelIdentifier.fromLanguageTag("zh")
            ?: throw IllegalArgumentException("Chinese language model not available")
    }

    private val enModelIdentifier by lazy {
        DigitalInkRecognitionModelIdentifier.fromLanguageTag("en")
            ?: throw IllegalArgumentException("English language model not available")
    }

    // Models for different languages
    private val zhModel by lazy {
        DigitalInkRecognitionModel.builder(zhModelIdentifier).build()
    }

    private val enModel by lazy {
        DigitalInkRecognitionModel.builder(enModelIdentifier).build()
    }

    // Recognizers for different languages
    private var zhRecognizer: DigitalInkRecognizer? = null
    private var enRecognizer: DigitalInkRecognizer? = null

    // Download status
    private var zhModelDownloaded = false
    private var enModelDownloaded = false

    // Flag to track initialization attempt
    private var initializationAttempted = false

    // Initialize by downloading models
    suspend fun initialize() {
        if (initializationAttempted) return

        try {
            initializationAttempted = true

            // Check if models are already downloaded
            val downloadConditions = DownloadConditions.Builder()
                .requireWifi()  // Only download on Wi-Fi to avoid using mobile data
                .build()

            try {
                // Check Chinese model
                val zhModelExists = modelManager.isModelDownloaded(zhModel).await()
                if (!zhModelExists) {
                    Log.d("HandwritingRecognizer", "Downloading Chinese model...")
                    modelManager.download(zhModel, downloadConditions).await()
                    Log.d("HandwritingRecognizer", "Chinese model downloaded")
                } else {
                    Log.d("HandwritingRecognizer", "Chinese model already exists")
                }

                zhModelDownloaded = true
                zhRecognizer = DigitalInkRecognition.getClient(
                    DigitalInkRecognizerOptions.builder(zhModel).build()
                )
            } catch (e: Exception) {
                Log.e("HandwritingRecognizer", "Failed to download Chinese model", e)
                // Continue with English model even if Chinese fails
            }

            try {
                // Check English model
                val enModelExists = modelManager.isModelDownloaded(enModel).await()
                if (!enModelExists) {
                    Log.d("HandwritingRecognizer", "Downloading English model...")
                    modelManager.download(enModel, downloadConditions).await()
                    Log.d("HandwritingRecognizer", "English model downloaded")
                } else {
                    Log.d("HandwritingRecognizer", "English model already exists")
                }

                enModelDownloaded = true
                enRecognizer = DigitalInkRecognition.getClient(
                    DigitalInkRecognizerOptions.builder(enModel).build()
                )
            } catch (e: Exception) {
                Log.e("HandwritingRecognizer", "Failed to download English model", e)
                // Continue with Chinese model even if English fails
            }

            Log.d("HandwritingRecognizer", "Models initialized: Chinese=${zhModelDownloaded}, English=${enModelDownloaded}")
        } catch (e: Exception) {
            Log.e("HandwritingRecognizer", "Error initializing models", e)
            // Don't throw here, as we'll return fallback suggestions if recognition fails
        }
    }

    // Check if recognizers are ready
    private fun isReady(): Boolean {
        val ready = (zhModelDownloaded && zhRecognizer != null) ||
                (enModelDownloaded && enRecognizer != null)
        Log.d("HandwritingRecognizer", "Recognition models ready: $ready")
        return ready
    }

    // Recognize handwriting
    suspend fun recognizeHandwriting(strokes: List<List<Offset>>): List<String> {
        if (!isReady()) {
            Log.w("HandwritingRecognizer", "Models not ready, initializing...")
            initialize()

            // If still not ready after initialization, return fallback suggestions
            if (!isReady()) {
                Log.w("HandwritingRecognizer", "Models still not ready after initialization, returning fallbacks")
                return listOf("你", "我", "的", "是", "了")
            }
        }

        if (strokes.isEmpty()) {
            Log.d("HandwritingRecognizer", "No strokes to recognize")
            return emptyList()
        }

        val results = mutableListOf<String>()

        try {
            // Create ink object
            val inkBuilder = Ink.builder()

            // Add each stroke separately
            strokes.forEach { strokePoints ->
                if (strokePoints.isNotEmpty()) {
                    val strokeBuilder = Ink.Stroke.builder()
                    strokePoints.forEach { point ->
                        strokeBuilder.addPoint(Ink.Point.create(point.x, point.y))
                    }
                    inkBuilder.addStroke(strokeBuilder.build())
                }
            }

            val ink = inkBuilder.build()
            Log.d("HandwritingRecognizer", "Created ink with ${strokes.size} strokes")

            // Try Chinese recognition if available
            if (zhModelDownloaded && zhRecognizer != null) {
                try {
                    val zhResults = recognizeWithModel(ink, zhRecognizer!!)
                    Log.d("HandwritingRecognizer", "Chinese recognition results: $zhResults")
                    results.addAll(zhResults.take(3))  // Take top 3 Chinese results
                } catch (e: Exception) {
                    Log.e("HandwritingRecognizer", "Chinese recognition failed", e)
                }
            }

            // Then try English recognition if available
            if (enModelDownloaded && enRecognizer != null) {
                try {
                    val enResults = recognizeWithModel(ink, enRecognizer!!)
                    Log.d("HandwritingRecognizer", "English recognition results: $enResults")
                    results.addAll(enResults.take(2))  // Take top 2 English results
                } catch (e: Exception) {
                    Log.e("HandwritingRecognizer", "English recognition failed", e)
                }
            }

            // Return unique results, or fallback if empty
            val uniqueResults = results.distinct().take(5)
            return uniqueResults.ifEmpty {
                listOf("你", "我", "的", "是", "了")
            }
        } catch (e: Exception) {
            Log.e("HandwritingRecognizer", "Recognition failed", e)
            return listOf("你", "我", "的", "是", "了")
        }
    }

    // Helper function to perform recognition with a specific model
    private suspend fun recognizeWithModel(
        ink: Ink,
        recognizer: DigitalInkRecognizer
    ): List<String> {
        return suspendCancellableCoroutine { continuation ->
            recognizer.recognize(ink)
                .addOnSuccessListener { result ->
                    val candidates = result.candidates.map { it.text }
                    continuation.resume(candidates)
                }
                .addOnFailureListener { e ->
                    Log.e("HandwritingRecognizer", "Recognition failed", e)
                    continuation.resume(emptyList())
                }
        }
    }

    // Clean up resources
    fun close() {
        try {
            zhRecognizer?.close()
            enRecognizer?.close()
            Log.d("HandwritingRecognizer", "Closed recognizers")
        } catch (e: Exception) {
            Log.e("HandwritingRecognizer", "Error closing recognizers", e)
        }
    }
}
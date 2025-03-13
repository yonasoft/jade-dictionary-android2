package com.yonasoft.jadedictionary.features.ocr.domain.services

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OCRService(private val context: Context) {

    // Initialize the OCR recognizer with Chinese options
    private val recognizer: TextRecognizer by lazy {
        // Create an instance of the options class
        val options = ChineseTextRecognizerOptions.Builder().build()
        TextRecognition.getClient(options)
    }

    /**
     * Process an image and extract text using OCR
     */
    suspend fun recognizeText(bitmap: Bitmap): List<String> {
        return try {
            // Create input image
            val image = InputImage.fromBitmap(bitmap, 0)

            // Process the image and get text blocks
            val result = processImage(image)

            // Extract and return a list of detected text blocks
            extractTextBlocks(result)
        } catch (e: Exception) {
            Log.e("OCRService", "Error recognizing text", e)
            emptyList()
        }
    }

    /**
     * Process an image from a Uri
     */
    suspend fun recognizeText(imageUri: Uri): List<String> {
        return try {
            // Create input image from URI
            val image = InputImage.fromFilePath(context, imageUri)

            // Process the image and get text blocks
            val result = processImage(image)

            // Extract and return a list of detected text blocks
            extractTextBlocks(result)
        } catch (e: Exception) {
            Log.e("OCRService", "Error recognizing text from URI", e)
            emptyList()
        }
    }

    /**
     * Process image with ML Kit text recognizer
     */
    private suspend fun processImage(image: InputImage): Text {
        return suspendCancellableCoroutine { continuation ->
            recognizer.process(image).addOnSuccessListener { text ->
                    continuation.resume(text)
                }.addOnFailureListener { e ->
                    Log.e("OCRService", "Text recognition failed", e)
                    // Instead of trying to create a Text object, just resume with exception
                    // which will be caught in the calling function
                    continuation.resumeWithException(e)
                }
        }
    }

    /**
     * Extract text blocks from the OCR result
     */
    private fun extractTextBlocks(text: Text): List<String> {
        val results = mutableListOf<String>()

        // First, try to extract individual characters (better for Chinese)
        for (block in text.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    results.add(element.text)
                }
            }
        }

        // If no individual elements were found, fall back to lines
        if (results.isEmpty()) {
            for (block in text.textBlocks) {
                for (line in block.lines) {
                    results.add(line.text)
                }
            }
        }

        // If still empty, use entire blocks
        if (results.isEmpty()) {
            for (block in text.textBlocks) {
                results.add(block.text)
            }
        }

        return results.filter { it.isNotEmpty() }.distinct()
    }

    /**
     * Clean up resources
     */
    fun close() {
        recognizer.close()
    }
}
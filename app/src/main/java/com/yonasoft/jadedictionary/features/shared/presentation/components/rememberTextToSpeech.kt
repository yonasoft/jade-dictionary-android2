package com.yonasoft.jadedictionary.features.shared.presentation.components

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun rememberTextToSpeech(locale: Locale): MutableState<TextToSpeech?> {
    val context = LocalContext.current
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = locale
            }
        }
        tts.value = textToSpeech

        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
    return tts
}

fun openTTS(tts: TextToSpeech, text: String, setSpeaking: (Boolean) -> Unit) {
    if (tts.isSpeaking) {
        tts.stop()
        setSpeaking(false)
    } else {
        tts.speak(
            text, TextToSpeech.QUEUE_FLUSH, null, ""
        )
        setSpeaking(true)
    }
}
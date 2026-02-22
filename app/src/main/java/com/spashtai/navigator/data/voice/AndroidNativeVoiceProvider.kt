package com.spashtai.navigator.data.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class AndroidNativeVoiceProvider(private val context: Context) : IVoiceProvider, RecognitionListener, TextToSpeech.OnInitListener {

    companion object {
        private const val TAG = "AndroidNativeVoiceProvider"
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null

    private val _isListening = MutableStateFlow(false)
    override val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    // We'll use this flow to emit final results.
    // For partial results, we could store them separately if needed,
    // but for now let's just emit final recognized text here or handle it via a callback?
    // The Interface defined `spokenText` as a Flow. Let's use that for the recognized text.
    private val _spokenText = MutableStateFlow("")
    override val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(this)
        }
        textToSpeech = TextToSpeech(context, this)
    }

    override fun startListening() {
        if (speechRecognizer == null) {
            Log.e(TAG, "SpeechRecognizer is not available on this device")
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            // Support both English and Hindi (Hinglish)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN") // Hindi
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
            // Add English as fallback
            putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("en-IN", "en-US"))
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            // Request lower confidence threshold for better recognition
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3000L)
        }

        try {
            speechRecognizer?.startListening(intent)
            _isListening.value = true
            _spokenText.value = "" // Reset on new listen
            _errorMessage.value = null // Clear previous errors
            Log.d(TAG, "Started listening for speech input (Hindi/Hinglish mode)")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognition: ${e.message}")
            _isListening.value = false
            _errorMessage.value = "Failed to start listening: ${e.message}"
        }
    }

    override fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
    }

    override fun speak(text: String) {
        // Stop any ongoing speech first
        textToSpeech?.stop()
        
        // Clean text: remove Markdown symbols like ** and sanitize for better TTS
        val cleanedText = cleanTextForSpeech(text)
        
        // Speak with proper locale
        textToSpeech?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, null, null)
        Log.d(TAG, "Speaking cleaned text: ${cleanedText.take(50)}...")
    }

    private fun cleanTextForSpeech(text: String): String {
        return text.replace("**", "")
            .replace("__", "")
            .replace("#", "")
            .replace("* ", "")
            .replace("- ", "")
            .replace("`", "")
            .trim()
    }

    override fun stopSpeaking() {
        textToSpeech?.stop()
        Log.d(TAG, "Stopped speaking")
    }

    override fun shutdown() {
        speechRecognizer?.destroy()
        textToSpeech?.shutdown()
    }

    // RecognitionListener
    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {
        _isListening.value = false
    }
    
    override fun onError(error: Int) {
        _isListening.value = false
        val (logMessage, userMessage) = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> Pair("Audio recording error", "Microphone error. Please check your microphone.")
            SpeechRecognizer.ERROR_CLIENT -> Pair("Client side error", "Speech recognition error. Please try again.")
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Pair("Insufficient permissions", "Microphone permission required.")
            SpeechRecognizer.ERROR_NETWORK -> Pair("Network error", "Network error. Please check your internet connection.")
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> Pair("Network timeout", "Network timeout. Please try again.")
            SpeechRecognizer.ERROR_NO_MATCH -> Pair("No speech match", "Couldn't understand. Please speak clearly and try again.")
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> Pair("Recognition service busy", "Speech service is busy. Please wait and try again.")
            SpeechRecognizer.ERROR_SERVER -> Pair("Server error", "Server error. Please try again later.")
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> Pair("No speech input", "No speech detected. Please speak after tapping the mic button.")
            else -> Pair("Unknown error: $error", "Speech recognition failed. Please try again.")
        }
        Log.e(TAG, "Speech recognition error: $logMessage")

        // Only show error message for critical errors, not for "no match" or "timeout"
        if (error != SpeechRecognizer.ERROR_NO_MATCH && error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
            _errorMessage.value = userMessage
        } else {
            // For no match/timeout, just log but don't alarm the user
            _errorMessage.value = null
        }
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val recognizedText = matches[0]
            _spokenText.value = recognizedText
            Log.d(TAG, "Speech recognized: $recognizedText")
        }
        _isListening.value = false
    }

    override fun onPartialResults(partialResults: Bundle?) {
        // Optional: Update UI with what is being said so far
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val partialText = matches[0]
            _spokenText.value = partialText
            Log.d(TAG, "Partial result: $partialText")
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    // TTS Init
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set Hindi as primary language for proper accent
            val hindiLocale = Locale("hi", "IN")
            val result = textToSpeech?.setLanguage(hindiLocale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(TAG, "Hindi language not supported, falling back to default locale")
                textToSpeech?.language = Locale.getDefault()
            } else {
                Log.d(TAG, "TTS configured for Hindi (hi-IN)")
            }

            // Set speech rate slightly slower for clarity
            textToSpeech?.setSpeechRate(0.9f)
        } else {
            Log.e(TAG, "TTS initialization failed with status: $status")
        }
    }
}

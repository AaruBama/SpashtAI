package com.ashaai.navigator.data.voice

import kotlinx.coroutines.flow.StateFlow

interface IVoiceProvider {
    val isListening: StateFlow<Boolean>
    val spokenText: StateFlow<String> // Real-time partial results
    val errorMessage: StateFlow<String?> // Error messages for UI

    fun startListening()
    fun stopListening()
    fun speak(text: String)
    fun stopSpeaking()
    fun shutdown()
}

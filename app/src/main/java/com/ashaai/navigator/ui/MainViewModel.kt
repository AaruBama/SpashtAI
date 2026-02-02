package com.ashaai.navigator.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashaai.navigator.data.ai.GeminiHealthService
import com.ashaai.navigator.data.model.AnalysisRequest
import com.ashaai.navigator.data.model.AnalysisResponse
import com.ashaai.navigator.data.remote.RetrofitClient
import com.ashaai.navigator.data.model.ChatRequest
import com.ashaai.navigator.data.model.Message
import com.ashaai.navigator.data.voice.AndroidNativeVoiceProvider
import com.ashaai.navigator.data.voice.IVoiceProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AnalysisState {
    object Idle : AnalysisState()
    object Loading : AnalysisState()
    data class Success(val data: AnalysisResponse) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val voiceProvider: IVoiceProvider = AndroidNativeVoiceProvider(application.applicationContext)
    private val geminiService = GeminiHealthService()

    val isListening = voiceProvider.isListening
    val voiceError = voiceProvider.errorMessage

    // No auto-send - voice input will populate text field for manual submission
    init {
        // Just initialize, no auto-send logic needed
    }

    // Expose voice input for live transcription display
    val voiceInput = voiceProvider.spokenText


    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    // Track if AI is currently thinking/responding
    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Track if TTS is currently speaking
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    fun analyzeReport(imagePath: String) {
        viewModelScope.launch {
            _analysisState.value = AnalysisState.Loading
            try {
                // In a real app, convert imagePath to Base64 or Multipart here.
                // Sending dummy Base64 for demonstration as per requirement "generate boilerplate".
                val dummyBase64 = "base64_encoded_image_placeholder"

                val response = RetrofitClient.apiService.analyzeImage(AnalysisRequest(dummyBase64))
                _analysisState.value = AnalysisState.Success(response)
            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun toggleListening() {
        if (isListening.value) {
            voiceProvider.stopListening()
        } else {
            // Stop TTS when starting to listen (prevents capturing AI voice)
            if (_isSpeaking.value) {
                stopSpeaking()
            }
            voiceProvider.startListening()
        }
    }

    fun speak(text: String) {
        _isSpeaking.value = true
        voiceProvider.speak(text)
        // Note: We can't track when TTS finishes without adding a listener
        // For now, we'll set it to false after a reasonable delay or when manually stopped
        viewModelScope.launch {
            kotlinx.coroutines.delay(text.length * 50L) // Rough estimate
            _isSpeaking.value = false
        }
    }

    fun stopSpeaking() {
        voiceProvider.stopSpeaking()
        _isSpeaking.value = false
    }

    override fun onCleared() {
        super.onCleared()
        voiceProvider.stopSpeaking()
        voiceProvider.shutdown()
    }

    fun sendMessage(text: String) {
        val userMessage = Message(text = text, isUser = true)
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            _isAiThinking.value = true
            try {
                // Use Gemini for healthcare responses if configured
                if (geminiService.isConfigured()) {
                    // Build chat history for context
                    val chatHistory = _messages.value
                        .takeLast(10) // Last 5 exchanges (10 messages)
                        .filter { it.text.isNotBlank() }
                        .map { message ->
                            if (message.isUser) {
                                Pair("user", message.text)
                            } else {
                                Pair("model", message.text)
                            }
                        }

                    val result = geminiService.getChatResponse(text, chatHistory)

                    result.onSuccess { response ->
                        _isAiThinking.value = false
                        val botMessage = Message(text = response, isUser = false)
                        _messages.value = _messages.value + botMessage

                        // Auto-speak the response
                        speak(response)
                    }.onFailure { error ->
                        _isAiThinking.value = false
                        val errorText = "I'm having trouble connecting right now. Error: ${error.message}"
                        val botMessage = Message(text = errorText, isUser = false)
                        _messages.value = _messages.value + botMessage
                        speak("Sorry, I'm having trouble connecting.")
                    }
                } else {
                    // Fallback: Try REST API or show setup message
                    try {
                        val response = RetrofitClient.apiService.chat(ChatRequest(text))
                        _isAiThinking.value = false
                        val botMessage = Message(text = response.response, isUser = false)
                        _messages.value = _messages.value + botMessage
                        speak(response.response)
                    } catch (e: Exception) {
                        _isAiThinking.value = false
                        val setupText = "Please configure your Gemini API key in local.properties file. Add: GEMINI_API_KEY=your_key_here"
                        val botMessage = Message(text = setupText, isUser = false)
                        _messages.value = _messages.value + botMessage
                        speak("API key not configured.")
                    }
                }
            } catch (e: Exception) {
                _isAiThinking.value = false
                val errorMessage = Message(text = "Error: ${e.message}", isUser = false)
                _messages.value = _messages.value + errorMessage
                speak("Sorry, I encountered an error.")
            }
        }
    }
}

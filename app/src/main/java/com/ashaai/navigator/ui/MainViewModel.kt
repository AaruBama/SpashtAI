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

    // We observe spoken text and update the input field or auto-send
    init {
        viewModelScope.launch {
            voiceProvider.spokenText.collect { text ->
                if (text.isNotBlank() && !isListening.value) {
                    // If listening stopped and we have text, we could auto-send or just populate.
                    // For now, let's just populate the input field in UI via a state,
                    // OR we can't easily update the UI's local state `inputText` from here without a binding.
                    // So we'll expose `recognizedText` for the UI to consume.
                }
            }
        }
    }

    // To simple expose the flow
    val voiceInput = voiceProvider.spokenText


    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

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
            voiceProvider.startListening()
        }
    }

    fun speak(text: String) {
        voiceProvider.speak(text)
    }

    fun stopSpeaking() {
        voiceProvider.stopSpeaking()
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
                        val botMessage = Message(text = response, isUser = false)
                        _messages.value = _messages.value + botMessage

                        // Auto-speak the response
                        speak(response)
                    }.onFailure { error ->
                        val errorText = "I'm having trouble connecting right now. Error: ${error.message}"
                        val botMessage = Message(text = errorText, isUser = false)
                        _messages.value = _messages.value + botMessage
                        speak("Sorry, I'm having trouble connecting.")
                    }
                } else {
                    // Fallback: Try REST API or show setup message
                    try {
                        val response = RetrofitClient.apiService.chat(ChatRequest(text))
                        val botMessage = Message(text = response.response, isUser = false)
                        _messages.value = _messages.value + botMessage
                        speak(response.response)
                    } catch (e: Exception) {
                        val setupText = "Please configure your Gemini API key in local.properties file. Add: GEMINI_API_KEY=your_key_here"
                        val botMessage = Message(text = setupText, isUser = false)
                        _messages.value = _messages.value + botMessage
                        speak("API key not configured.")
                    }
                }
            } catch (e: Exception) {
                val errorMessage = Message(text = "Error: ${e.message}", isUser = false)
                _messages.value = _messages.value + errorMessage
                speak("Sorry, I encountered an error.")
            }
        }
    }
}

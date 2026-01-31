package com.ashaai.navigator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashaai.navigator.data.model.AnalysisRequest
import com.ashaai.navigator.data.model.AnalysisResponse
import com.ashaai.navigator.data.remote.RetrofitClient
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

class MainViewModel : ViewModel() {

    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

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
}

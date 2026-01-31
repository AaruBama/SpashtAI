package com.ashaai.navigator.data.remote

import com.ashaai.navigator.data.model.AnalysisRequest
import com.ashaai.navigator.data.model.AnalysisResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("analyze")
    suspend fun analyzeImage(@Body request: AnalysisRequest): AnalysisResponse
}

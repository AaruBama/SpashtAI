package com.ashaai.navigator.data.remote

import com.ashaai.navigator.data.model.AnalysisRequest
import com.ashaai.navigator.data.model.AnalysisResponse
import com.ashaai.navigator.data.model.ChatRequest
import com.ashaai.navigator.data.model.ChatResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("analyze")
    suspend fun analyzeImage(
        @Part image: MultipartBody.Part,
        @Part("prompt") prompt: RequestBody,
        @Part("history") history: RequestBody? = null
    ): AnalysisResponse

    @POST("chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}

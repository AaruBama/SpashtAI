package com.spashtai.navigator.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnalysisRequest(
    val image_data: String // Base64 encoded image
)

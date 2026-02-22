package com.ashaai.navigator.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnalysisResponse(
    val analysis: String
)

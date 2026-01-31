package com.ashaai.navigator.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnalysisResponse(
    val findings: String,
    val simplified_text: String
)

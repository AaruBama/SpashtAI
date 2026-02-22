package com.spashtai.navigator.data.model

data class ChatRequest(
    val prompt: String
)

data class ChatResponse(
    val response: String
)

data class Message(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

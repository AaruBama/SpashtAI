package com.spashtai.navigator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@Entity(tableName = "report_history")
@TypeConverters(MessageListConverter::class)
data class ReportHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String, // Auto-generated from first message or user input
    val reportFilePath: String, // Local file path to the report image/PDF
    val reportType: ReportType, // IMAGE or PDF
    val uploadTimestamp: Long = System.currentTimeMillis(),
    val messages: List<ChatMessage> = emptyList(), // Conversation about this report
    val summary: String? = null // AI-generated summary
)

enum class ReportType {
    IMAGE,
    PDF
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

// Type converter for Room to store list of messages as JSON
class MessageListConverter {
    private val moshi = Moshi.Builder()
        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()
    private val type = Types.newParameterizedType(List::class.java, ChatMessage::class.java)
    private val adapter: JsonAdapter<List<ChatMessage>> = moshi.adapter(type)

    @TypeConverter
    fun fromMessageList(messages: List<ChatMessage>?): String {
        return adapter.toJson(messages ?: emptyList())
    }

    @TypeConverter
    fun toMessageList(json: String): List<ChatMessage> {
        return adapter.fromJson(json) ?: emptyList()
    }
}

// Request model for report analysis
data class ReportAnalysisRequest(
    val reportUri: String,
    val userPrompt: String = "कृपया इस रिपोर्ट को समझाइए। क्या कोई चिंता की बात है?" // Default prompt in Hindi
)

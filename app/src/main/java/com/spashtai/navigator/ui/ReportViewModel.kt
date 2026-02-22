package com.spashtai.navigator.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.spashtai.navigator.data.ai.GeminiHealthService
import com.spashtai.navigator.data.database.AppDatabase
import com.spashtai.navigator.data.model.ChatMessage
import com.spashtai.navigator.data.model.ReportHistory
import com.spashtai.navigator.data.model.ReportType
import android.util.Log
import com.spashtai.navigator.data.remote.RetrofitClient
import com.spashtai.navigator.utils.FileUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

sealed class ReportAnalysisState {
    object Idle : ReportAnalysisState()
    object Analyzing : ReportAnalysisState()
    data class Success(val response: String) : ReportAnalysisState()
    data class Error(val message: String) : ReportAnalysisState()
}

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val geminiService = GeminiHealthService()
    private val reportDao = AppDatabase.getDatabase(application).reportDao()

    private val _analysisState = MutableStateFlow<ReportAnalysisState>(ReportAnalysisState.Idle)
    val analysisState: StateFlow<ReportAnalysisState> = _analysisState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _reportHistory = reportDao.getAllReports()
    val reportHistory = _reportHistory

    private var currentReportId: Long? = null
    private var currentReportUri: Uri? = null

    /**
     * Analyze a medical report
     */
    fun analyzeReport(reportUri: Uri, userPrompt: String) {
        _analysisState.value = ReportAnalysisState.Analyzing

        // Add user message
        val userMessage = ChatMessage(text = userPrompt, isUser = true)
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            try {
                // Save report file locally
                val savedFilePath = saveReportToLocal(reportUri)
                val context = getApplication<Application>()

                Log.d("ReportViewModel", "Attempting local MedGemma analysis for URI: $reportUri")
                // Only use local MedGemma server
                val result = try {
                    val imagePart = FileUtils.getMultipartPart(context, reportUri, "image")
                    if (imagePart != null) {
                        val promptBody = RequestBody.create(
                            "text/plain".toMediaTypeOrNull(),
                            userPrompt
                        )
                        val response = RetrofitClient.apiService.analyzeImage(imagePart, promptBody)
                        Log.d("ReportViewModel", "Local MedGemma successful")
                        Result.success(response.analysis)
                    } else {
                        Log.e("ReportViewModel", "Could not process image for local server")
                        Result.failure(Exception("Could not process image file"))
                    }
                } catch (e: Exception) {
                    Log.e("ReportViewModel", "Local MedGemma failed: ${e.message}")
                    Result.failure(e)
                }

                result.onSuccess { response ->
                    _analysisState.value = ReportAnalysisState.Success(response)

                    // Add AI response
                    val aiMessage = ChatMessage(text = response, isUser = false)
                    _messages.value = _messages.value + aiMessage

                    // Save to history
                    val report = ReportHistory(
                        title = generateReportTitle(userPrompt),
                        reportFilePath = savedFilePath,
                        reportType = determineReportType(reportUri),
                        messages = _messages.value,
                        summary = response.take(200) // First 200 chars as summary
                    )

                    currentReportId = reportDao.insertReport(report)
                    currentReportUri = reportUri

                }.onFailure { error ->
                    _analysisState.value = ReportAnalysisState.Error(error.message ?: "Unknown error")
                    val errorMessage = ChatMessage(
                        text = "Error: ${error.message}",
                        isUser = false
                    )
                    _messages.value = _messages.value + errorMessage
                }

            } catch (e: Exception) {
                _analysisState.value = ReportAnalysisState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Send follow-up message about the report
     */
    fun sendFollowUpMessage(text: String) {
        val userMessage = ChatMessage(text = text, isUser = true)
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            try {
                currentReportUri?.let { uri ->
                    val context = getApplication<Application>()
                    Log.d("ReportViewModel", "Attempting local MedGemma follow-up")
                    val result = try {
                        val imagePart = FileUtils.getMultipartPart(context, uri, "image")
                        if (imagePart != null) {
                            val promptBody = RequestBody.create(
                                "text/plain".toMediaTypeOrNull(),
                                text
                            )
                            val historyBody = RequestBody.create(
                                "text/plain".toMediaTypeOrNull(),
                                getHistoryJson()
                            )
                            val response = RetrofitClient.apiService.analyzeImage(imagePart, promptBody, historyBody)
                            Log.d("ReportViewModel", "Local MedGemma follow-up successful")
                            Result.success(response.analysis)
                        } else {
                            Log.e("ReportViewModel", "Could not process image for local follow-up")
                            Result.failure(Exception("Could not process image file"))
                        }
                    } catch (e: Exception) {
                        Log.e("ReportViewModel", "Local MedGemma follow-up failed: ${e.message}")
                        Result.failure(e)
                    }

                    result.onSuccess { response ->
                        val aiMessage = ChatMessage(text = response, isUser = false)
                        _messages.value = _messages.value + aiMessage

                        // Update history with new messages
                        currentReportId?.let { reportId ->
                            reportDao.getReportById(reportId)?.let { report ->
                                val updatedReport = report.copy(messages = _messages.value)
                                reportDao.updateReport(updatedReport)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    text = "Error: ${e.message}",
                    isUser = false
                )
                _messages.value = _messages.value + errorMessage
            }
        }
    }

    /**
     * Load report from history
     */
    fun loadReportFromHistory(reportId: Long) {
        viewModelScope.launch {
            reportDao.getReportById(reportId)?.let { report ->
                currentReportId = reportId
                currentReportUri = Uri.parse(report.reportFilePath)
                _messages.value = report.messages
                _analysisState.value = ReportAnalysisState.Success(
                    report.messages.lastOrNull { !it.isUser }?.text ?: ""
                )
            }
        }
    }

    /**
     * Delete report from history
     */
    fun deleteReport(reportId: Long) {
        viewModelScope.launch {
            reportDao.deleteReportById(reportId)
        }
    }

    /**
     * Clear current session
     */
    fun clearCurrentReport() {
        _messages.value = emptyList()
        _analysisState.value = ReportAnalysisState.Idle
        currentReportId = null
        currentReportUri = null
    }

    private fun saveReportToLocal(uri: Uri): String {
        val context = getApplication<Application>()
        val reportsDir = File(context.filesDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()

        // Determine file extension based on mime type
        val mimeType = context.contentResolver.getType(uri)
        val extension = when {
            mimeType?.contains("pdf") == true -> "pdf"
            mimeType?.contains("png") == true -> "png"
            mimeType?.contains("jpeg") == true || mimeType?.contains("jpg") == true -> "jpg"
            else -> "jpg"
        }

        val fileName = "report_${System.currentTimeMillis()}.$extension"
        val file = File(reportsDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file.absolutePath
    }

    private fun determineReportType(uri: Uri): ReportType {
        val context = getApplication<Application>()
        val mimeType = context.contentResolver.getType(uri)
        return if (mimeType?.contains("pdf") == true) {
            ReportType.PDF
        } else {
            ReportType.IMAGE
        }
    }

    private fun generateReportTitle(prompt: String): String {
        return if (prompt.length > 50) {
            prompt.take(47) + "..."
        } else {
            prompt
        }
    }
    private fun getHistoryJson(): String {
        val historyMessages = _messages.value.dropLast(1) // All except current question
        if (historyMessages.isEmpty()) return "[]"
        
        val jsonArray = org.json.JSONArray()
        historyMessages.forEach { msg ->
            val obj = org.json.JSONObject()
            obj.put("role", if (msg.isUser) "user" else "assistant")
            obj.put("content", msg.text)
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }
}

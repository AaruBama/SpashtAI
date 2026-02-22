package com.spashtai.navigator.data.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.spashtai.navigator.BuildConfig
import com.spashtai.navigator.utils.PdfUtils
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class GeminiHealthService {

    companion object {
        private const val TAG = "GeminiHealthService"
        private const val MODEL_NAME = "gemini-2.0-flash"

        // Healthcare-focused system prompt
        private const val SYSTEM_PROMPT = """
            You are Spasht AI, a compassionate healthcare assistant for rural India.

            **MANDATORY PROTOCOL (DEMO MODE)**:
            1. You MUST ask these TWO exact questions before providing ANY results:
               - "आपको यह लक्षण कब से महसूस हो रहे हैं?"
               - "क्या आपको इसके साथ बुखार या कोई और परेशानी भी है?"
            2. CRITICAL: Do not give a diagnosis or home remedy until Step 2 is done unless user explicitly asks for it.
        """
    }

    private val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    /**
     * Get healthcare response from Gemini with medical context
     */
    suspend fun getHealthcareResponse(userMessage: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Sending healthcare query to Gemini: $userMessage")

                val fullPrompt = "$SYSTEM_PROMPT\n\nUser Message: $userMessage"
                val response: GenerateContentResponse = generativeModel.generateContent(fullPrompt)

                val responseText = response.text ?: throw Exception("Empty response from Gemini")

                Log.d(TAG, "Received response from Gemini: ${responseText.take(100)}...")

                Result.success(responseText)
            } catch (e: Exception) {
                Log.e(TAG, "Error getting Gemini response: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Get response using chat history for contextual conversation
     */
    suspend fun getChatResponse(
        userMessage: String,
        chatHistory: List<Pair<String, String>> = emptyList()
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Sending chat message to Gemini with history size: ${chatHistory.size}")

                // ALWAYS prepend instructions to ensure the model follows demo rules
                val demoPrompt = """
                    $SYSTEM_PROMPT
                    
                    User's latest message: $userMessage
                """.trimIndent()

                val chat = generativeModel.startChat(
                    history = chatHistory.map { (role, text) ->
                        content(role) { this.text(text) }
                    }
                )

                val response = chat.sendMessage(demoPrompt)
                val responseText = response.text ?: throw Exception("Empty response from Gemini")

                Log.d(TAG, "Received chat response from Gemini")

                Result.success(responseText)
            } catch (e: Exception) {
                Log.e(TAG, "Error in chat response: ${e.message}", e)

                // Provide helpful error messages
                val userFriendlyMessage = when {
                    e.message?.contains("API has not been used") == true ||
                    e.message?.contains("is disabled") == true -> {
                        "Please enable Generative Language API at: https://console.developers.google.com/apis/api/generativelanguage.googleapis.com"
                    }
                    e.message?.contains("API_KEY_INVALID") == true -> {
                        "Invalid API key. Get a new one from https://aistudio.google.com/app/apikey"
                    }
                    e.message?.contains("quota") == true -> {
                        "API quota exceeded. Please try again later or check your billing."
                    }
                    else -> e.message ?: "Unable to connect to Gemini AI"
                }

                Result.failure(Exception(userFriendlyMessage))
            }
        }
    }

    /**
     * Analyze medical report (image or PDF) with user prompt
     */
    suspend fun analyzeReportImage(
        context: Context,
        imageUri: Uri,
        userPrompt: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Analyzing report: $imageUri with prompt: $userPrompt")

                // Detect if it's a PDF or image
                val mimeType = context.contentResolver.getType(imageUri)
                val isPdf = mimeType == "application/pdf" || imageUri.toString().endsWith(".pdf")

                // Build report analysis prompt
                val fullPrompt = buildReportAnalysisPrompt(userPrompt)

                val response = if (isPdf) {
                    Log.d(TAG, "Processing as PDF document (multi-page)")

                    // Get all pages from PDF
                    val bitmaps = PdfUtils.pdfToAllBitmaps(context, imageUri, maxPages = 10)

                    if (bitmaps.isEmpty()) {
                        return@withContext Result.failure(Exception("Failed to load PDF pages"))
                    }

                    Log.d(TAG, "Loaded ${bitmaps.size} pages from PDF")

                    // Use Gemini Vision API with all pages
                    val pdfResponse = generativeModel.generateContent(
                        content {
                            // Add all pages as images
                            bitmaps.forEach { bitmap ->
                                image(bitmap)
                            }
                            text(fullPrompt)
                        }
                    )

                    // Clean up bitmaps
                    bitmaps.forEach { it.recycle() }

                    pdfResponse
                } else {
                    Log.d(TAG, "Processing as image")

                    val bitmap = loadBitmapFromUri(context, imageUri)
                    if (bitmap == null) {
                        return@withContext Result.failure(Exception("Failed to load image"))
                    }

                    // Use Gemini Vision API with single image
                    val imageResponse = generativeModel.generateContent(
                        content {
                            image(bitmap)
                            text(fullPrompt)
                        }
                    )

                    // Clean up bitmap
                    bitmap.recycle()

                    imageResponse
                }

                val responseText = response.text ?: throw Exception("Empty response from Gemini")
                Log.d(TAG, "Report analysis complete")

                Result.success(responseText)
            } catch (e: Exception) {
                Log.e(TAG, "Error analyzing report: ${e.javaClass.simpleName} - ${e.message}", e)
                e.printStackTrace()
                val userFriendlyMessage = when {
                    e.message?.contains("quota") == true -> "API quota exceeded. Please try again later."
                    e.message?.contains("timeout") == true -> "Analysis timed out. Try a clearer image."
                    e.message?.contains("too large") == true -> "File too large. Try a smaller file."
                    else -> e.message ?: "Unable to analyze report"
                }
                Result.failure(Exception(userFriendlyMessage))
            }
        }
    }

    /**
     * Load bitmap from URI with proper scaling and compression
     */
    private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) {
                Log.e(TAG, "Failed to decode bitmap from URI")
                return null
            }

            Log.d(TAG, "Original bitmap size: ${originalBitmap.width}x${originalBitmap.height}")

            // Scale down more aggressively (max 768x768) to reduce API processing time
            // Smaller size works better for images with small text
            val maxSize = 768
            val scaledBitmap = if (originalBitmap.width > maxSize || originalBitmap.height > maxSize) {
                val scaleFactor = maxSize.toFloat() / maxOf(originalBitmap.width, originalBitmap.height)
                val scaled = Bitmap.createScaledBitmap(
                    originalBitmap,
                    (originalBitmap.width * scaleFactor).toInt(),
                    (originalBitmap.height * scaleFactor).toInt(),
                    true // Use high quality filtering for better text readability
                )
                // Recycle original bitmap to free memory
                if (scaled != originalBitmap) {
                    originalBitmap.recycle()
                }
                scaled
            } else {
                originalBitmap
            }

            Log.d(TAG, "Bitmap loaded and scaled: ${scaledBitmap.width}x${scaledBitmap.height}")
            scaledBitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap: ${e.message}", e)
            null
        }
    }

    /**
     * Build report-specific analysis prompt
     */
    private fun buildReportAnalysisPrompt(userPrompt: String): String {
        return """
You are SpashtAI, a medical report analysis assistant for rural India.

**Task**: Analyze this medical report and answer the user's question.

**User's Question**: $userPrompt

**Response Guidelines**:
- Keep response UNDER 250 words
- Use simple Hindi or Hinglish
- Explain medical terms in simple language
- Highlight abnormal values or concerns
- Suggest when to see a doctor

**Format**:
1. Report summary (2-3 lines)
2. Key findings (bullet points)
3. Concerns if any (be clear but not alarming)
4. Next steps recommendation

**Safety**: Always remind user to consult a doctor for proper medical advice.
"""
    }

    /**
     * Check if API key is configured
     */
    fun isConfigured(): Boolean {
        val isConfigured = BuildConfig.GEMINI_API_KEY.isNotBlank() &&
                          BuildConfig.GEMINI_API_KEY != "YOUR_API_KEY_HERE"
        if (!isConfigured) {
            Log.w(TAG, "Gemini API key not configured. Please add GEMINI_API_KEY to local.properties")
        }
        return isConfigured
    }
}

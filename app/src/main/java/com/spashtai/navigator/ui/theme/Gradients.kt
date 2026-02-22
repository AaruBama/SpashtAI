package com.spashtai.navigator.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object SpashtAIGradients {
    // Deep Navy Blue gradient for Voice Diagnostic
    val voiceDiagnosticBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0A4D68),
            Color(0xFF0D6A92)
        )
    )

    // Bright Cyan gradient for Report Scanner
    val reportScannerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF00D9FF),
            Color(0xFF00B8E6)
        )
    )

    // Emerald Green gradient for Acoustic Check
    val acousticCheckBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF10B981),
            Color(0xFF059669)
        )
    )

    // Light gray for More Options
    val moreOptionsBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE8E8E8),
            Color(0xFFD8D8D8)
        )
    )

    // Subtle gradient for Patient Insight Card
    val patientInsightBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF9FAFB),
            Color(0xFFF3F4F6)
        )
    )
}

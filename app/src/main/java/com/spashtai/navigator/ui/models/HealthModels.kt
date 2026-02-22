package com.spashtai.navigator.ui.models

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector

data class HealthTip(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val gradient: Brush
)

data class HealthSummary(
    val score: Int,
    val scoreLabel: String,
    val nextStepTitle: String,
    val nextStepDescription: String
)

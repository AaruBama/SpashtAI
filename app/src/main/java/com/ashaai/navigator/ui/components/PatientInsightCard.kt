package com.ashaai.navigator.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashaai.navigator.ui.theme.AshaAIGradients

@Composable
fun PatientInsightCard(
    medicalTerm: String,
    simplifiedText: String,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.background(AshaAIGradients.patientInsightBrush)) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Loading indicator at the top
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Clinical Finding Section (Top - Card within card)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Clinical Finding",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (isLoading) {
                            // Shimmer effect for loading
                            ShimmerEffect(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                            )
                        } else {
                            Text(
                                text = medicalTerm,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Monospace, // Monospace for lab report feel
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 28.sp
                            )
                        }
                    }
                }

                // Spasht (Simplified) Section (Bottom - Warm background)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0).copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Spasht (Simplified)",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFFE65100), // Dark Orange
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color(0xFF4CAF50), // Green checkmark
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isLoading) {
                            // Shimmer effect for loading
                            ShimmerEffect(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = simplifiedText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 18.sp, // Larger font
                                    color = Color(0xFF424242), // Dark Gray
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.LightGray.copy(alpha = 0.3f),
                        Color.LightGray.copy(alpha = 0.5f),
                        Color.LightGray.copy(alpha = 0.3f)
                    ),
                    start = Offset(shimmerTranslate - 1000f, 0f),
                    end = Offset(shimmerTranslate, 0f)
                ),
                shape = RoundedCornerShape(8.dp)
            )
    )
}

@Preview
@Composable
fun PatientInsightCardPreview() {
    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PatientInsightCard(
                medicalTerm = "Pleural Effusion",
                simplifiedText = "फेफड़ों में पानी (Water in lungs)",
                isLoading = false
            )
            PatientInsightCard(
                medicalTerm = "Loading...",
                simplifiedText = "Loading...",
                isLoading = true
            )
        }
    }
}

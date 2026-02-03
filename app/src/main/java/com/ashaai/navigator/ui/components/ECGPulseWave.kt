package com.ashaai.navigator.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ashaai.navigator.ui.theme.Cyan

@Composable
fun ECGPulseWave(
    modifier: Modifier = Modifier,
    color: Color = Cyan,
    backgroundColor: Color = Color(0xFF1A1A2E)
) {
    var animationProgress by remember { mutableStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "ecg_pulse")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ecg_progress"
    )

    LaunchedEffect(progress) {
        animationProgress = progress
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f

            val path = Path()
            val segmentWidth = width / 10f
            val animOffset = animationProgress * width

            // Draw baseline
            path.moveTo(0f, centerY)

            // Create ECG wave pattern
            for (i in 0..10) {
                val x = i * segmentWidth - animOffset

                if (x < -segmentWidth || x > width + segmentWidth) continue

                when (i % 10) {
                    // P wave (small bump)
                    2 -> {
                        path.lineTo(x, centerY)
                        path.quadraticBezierTo(
                            x + segmentWidth * 0.25f, centerY - 15f,
                            x + segmentWidth * 0.5f, centerY
                        )
                    }
                    // QRS complex (sharp spike)
                    4 -> {
                        path.lineTo(x, centerY)
                        path.lineTo(x + segmentWidth * 0.2f, centerY + 20f) // Q
                        path.lineTo(x + segmentWidth * 0.4f, centerY - 80f) // R (tall spike)
                        path.lineTo(x + segmentWidth * 0.6f, centerY + 15f) // S
                        path.lineTo(x + segmentWidth * 0.8f, centerY)
                    }
                    // T wave (medium bump)
                    6 -> {
                        path.lineTo(x, centerY)
                        path.quadraticBezierTo(
                            x + segmentWidth * 0.3f, centerY - 25f,
                            x + segmentWidth * 0.6f, centerY
                        )
                    }
                    else -> {
                        path.lineTo(x + segmentWidth, centerY)
                    }
                }
            }

            // Draw the ECG line
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 3f)
            )

            // Draw moving dot at the end of the wave
            val dotX = (animationProgress * width).coerceIn(0f, width)
            drawCircle(
                color = color,
                radius = 6f,
                center = Offset(dotX, centerY)
            )
        }
    }
}

package com.ashaai.navigator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashaai.navigator.ui.theme.Primary
import com.ashaai.navigator.ui.theme.CardBorder
import com.ashaai.navigator.ui.theme.Gray500

@Composable
fun HealthSummaryCard(
    score: Int,
    scoreLabel: String,
    nextStepTitle: String,
    nextStepDescription: String,
    onBookAppointment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Score section with circular progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Overall Health Score",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray500,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = scoreLabel,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }

                // Circular progress indicator
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp)
                ) {
                    CircularProgressIndicator(
                        progress = score / 100f,
                        modifier = Modifier.size(80.dp),
                        color = Primary,
                        strokeWidth = 8.dp,
                        trackColor = Color(0xFFf3f4f6)
                    )
                    Text(
                        text = "$score%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Divider
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFf9fafb))
            Spacer(modifier = Modifier.height(16.dp))

            // Next step section
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.EventAvailable,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = nextStepTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = nextStepDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray500,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Book appointment button
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onBookAppointment,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Book Appointment",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun HealthSummaryCardPreview() {
    HealthSummaryCard(
        score = 85,
        scoreLabel = "Excellent",
        nextStepTitle = "Next Step",
        nextStepDescription = "Schedule your annual check-up to keep your score high.",
        onBookAppointment = {}
    )
}

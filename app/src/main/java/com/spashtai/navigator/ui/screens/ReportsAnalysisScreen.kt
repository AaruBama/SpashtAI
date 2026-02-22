package com.spashtai.navigator.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spashtai.navigator.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

data class AnalyzedReport(
    val id: String,
    val filename: String,
    val uploadDate: String,
    val fileSize: String,
    val status: ReportStatus,
    val icon: ReportIcon
)

enum class ReportStatus {
    ANALYZING,
    READY
}

enum class ReportIcon {
    RADIOLOGY,
    BIOTECH,
    DESCRIPTION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsAnalysisScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChat: (Uri) -> Unit
) {
    val context = LocalContext.current
    var recentReports by remember {
        mutableStateOf(
            listOf(
                AnalyzedReport(
                    id = "1",
                    filename = "Chest X-Ray_042.dicom",
                    uploadDate = "3 mins ago",
                    fileSize = "14.2 MB",
                    status = ReportStatus.ANALYZING,
                    icon = ReportIcon.RADIOLOGY
                ),
                AnalyzedReport(
                    id = "2",
                    filename = "Brain MRI_Section_B.pdf",
                    uploadDate = "Oct 24, 2023",
                    fileSize = "28.5 MB",
                    status = ReportStatus.READY,
                    icon = ReportIcon.BIOTECH
                ),
                AnalyzedReport(
                    id = "3",
                    filename = "Lumbar CT Scan.jpg",
                    uploadDate = "Oct 21, 2023",
                    fileSize = "5.1 MB",
                    status = ReportStatus.READY,
                    icon = ReportIcon.DESCRIPTION
                )
            )
        )
    }

    // File picker for medical reports
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Take persistable URI permission
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            // Navigate to chat for analysis
            onNavigateToChat(it)
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }

                        Text(
                            text = "Medical Reports",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        IconButton(
                            onClick = { /* Info */ },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Info",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }

            // AI Insight Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Primary.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = "How our AI works",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Text(
                            text = "Our AI scans pixels for anomalies, cross-references with clinical databases, and flags areas for doctor review. Analysis typically takes 2-5 minutes.",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = Gray600
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { /* Learn more */ }
                        ) {
                            Text(
                                text = "Learn about accuracy",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Primary Upload Action
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Gradient glow effect
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .blur(20.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Primary.copy(alpha = 0.25f),
                                        Color(0xFF60A5FA).copy(alpha = 0.25f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                    )

                    // Upload button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                filePicker.launch(
                                    arrayOf(
                                        "image/*",
                                        "application/pdf",
                                        "application/dicom"
                                    )
                                )
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(
                            width = 2.dp,
                            color = Primary.copy(alpha = 0.3f),
                            // Dashed border would require custom drawing
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp, 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Primary.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Upload Scan or Report",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Supports DICOM, PDF, JPG, PNG",
                                    fontSize = 14.sp,
                                    color = Gray600
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("X-Ray", "CT Scan", "MRI").forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color(0xFFF3F4F6)
                                    ) {
                                        Text(
                                            text = tag,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Gray500,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // HIPAA compliance badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF10b981),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "HIPAA Compliant & End-to-End Encrypted",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gray600
                    )
                }
            }

            // Recently Analyzed Reports
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recently Analyzed",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    TextButton(onClick = { /* View all */ }) {
                        Text(
                            text = "View All",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                    }
                }
            }

            // Report items
            items(recentReports) { report ->
                ReportListItem(
                    report = report,
                    onClick = {
                        // For demo, we navigate to chat with a dummy URI or handled if real history exists
                        // In a real app, we'd use the stored report file path
                    }
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ReportListItem(
    report: AnalyzedReport,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (report.icon) {
                        ReportIcon.RADIOLOGY -> Icons.Default.MedicalServices
                        ReportIcon.BIOTECH -> Icons.Default.Biotech
                        ReportIcon.DESCRIPTION -> Icons.Default.Description
                    },
                    contentDescription = null,
                    tint = Gray600,
                    modifier = Modifier.size(24.dp)
                )
            }

            // File info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = report.filename,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${report.uploadDate} • ${report.fileSize}",
                    fontSize = 12.sp,
                    color = Gray600
                )
            }

            // Status badge
            when (report.status) {
                ReportStatus.ANALYZING -> {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Primary.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PulsingDot()
                            Text(
                                text = "Analyzing...",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                    }
                }
                ReportStatus.READY -> {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFD1FAE5)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Ready",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PulsingDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier.size(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pulsing outer circle
        Box(
            modifier = Modifier
                .size(8.dp * scale)
                .background(Primary.copy(alpha = 0.75f), CircleShape)
        )
        // Inner circle
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Primary, CircleShape)
        )
    }
}

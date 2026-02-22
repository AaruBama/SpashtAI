package com.spashtai.navigator.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.spashtai.navigator.ui.components.ECGPulseWave
import com.spashtai.navigator.ui.components.icons.aiBrainIcon
import com.spashtai.navigator.ui.components.icons.uploadReportIcon
import com.spashtai.navigator.ui.theme.Cyan
import com.spashtai.navigator.ui.theme.DeepNavyBlue
import com.spashtai.navigator.ui.theme.MediumGray
import com.spashtai.navigator.utils.PdfUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spashtai.navigator.ui.ReportViewModel
import com.spashtai.navigator.data.model.ChatMessage as ModelChatMessage
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UploadedReport(
    val uri: Uri,
    val filename: String,
    val sizeKB: Float,
    val isPdf: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScannerScreen(
    onNavigateBack: () -> Unit,
    onReportAnalyzed: (Uri, String) -> Unit,
    viewModel: ReportViewModel = viewModel()
) {
    var uploadedReports by remember { mutableStateOf<List<UploadedReport>>(emptyList()) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var currentAnalyzingReport by remember { mutableStateOf<UploadedReport?>(null) }
    var userMessage by remember { mutableStateOf("") }
    val chatMessages by viewModel.messages.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size + uploadedReports.size + 1)
        }
    }

    // File picker for images and PDFs
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Take persistable URI permission
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            
            val filename = it.lastPathSegment?.substringAfterLast('/') ?: "report.pdf"
            val mimeType = context.contentResolver.getType(it)
            val isPdf = mimeType == "application/pdf" || it.toString().endsWith(".pdf")
            
            // Get file size
            val sizeKB = try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    stream.available() / 1024f
                } ?: 0f
            } catch (e: Exception) {
                0f
            }
            
            val report = UploadedReport(it, filename, sizeKB, isPdf)
            currentAnalyzingReport = report
            isAnalyzing = true
            
            // Simulate analysis delay
            scope.launch {
                delay(3000) // Show ECG animation for 3 seconds
                uploadedReports = uploadedReports + report
                isAnalyzing = false
                currentAnalyzingReport = null
                
                // Start real analysis with Gemini
                viewModel.analyzeReport(
                    reportUri = report.uri,
                    userPrompt = "Please explain this medical report in simple terms. कृपया इस रिपोर्ट को सरल शब्दों में समझाएं।"
                )
            }
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF5F5F5),
                            onClick = onNavigateBack
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = DeepNavyBlue
                                )
                            }
                        }
                        
                        Column {
                            Text(
                                text = "Report Scanner",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavyBlue
                            )
                            Text(
                                text = "Upload and analyze medical reports",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MediumGray
                            )
                        }
                    }
                    
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Cyan,
                        onClick = { filePicker.launch(arrayOf("image/*", "application/pdf")) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = uploadReportIcon(Color.White),
                                contentDescription = "Upload",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uploadedReports.isEmpty()) {
            // Empty state - show upload area
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Upload area with dashed border
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .border(
                            width = 2.dp,
                            color = Cyan,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { filePicker.launch(arrayOf("image/*", "application/pdf")) },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF0F9FF)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = Cyan
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = uploadReportIcon(Color.White),
                                    contentDescription = "Upload",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Upload Medical Reports",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavyBlue
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Drag & drop or click to browse",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MediumGray
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = MediumGray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Images",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MediumGray
                                )
                            }
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = MediumGray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "PDF Files",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MediumGray
                                )
                            }
                        }
                    }
                }
                
                // How it works section
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFE0F7FA)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Cyan,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "How it works",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavyBlue
                            )
                            
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "• Upload your medical reports (blood tests, X-rays, etc.)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DeepNavyBlue
                                )
                                Text(
                                    text = "• AI will analyze and extract key information",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DeepNavyBlue
                                )
                                Text(
                                    text = "• Ask questions about your results in Hindi or English",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DeepNavyBlue
                                )
                                Text(
                                    text = "• Get instant explanations and recommendations",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DeepNavyBlue
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Show uploaded reports list with chat interface
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(bottom = 140.dp), // Space for input field
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Uploaded Reports (${uploadedReports.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavyBlue
                            )
                            
                            TextButton(onClick = { uploadedReports = emptyList() }) {
                                Text(
                                    text = "Clear All",
                                    color = Cyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    
                    items(uploadedReports) { report ->
                        UploadedReportCard(
                            report = report,
                            onDelete = {
                                uploadedReports = uploadedReports.filter { it != report }
                            },
                            onClick = {
                                onReportAnalyzed(report.uri, "कृपया इस रिपोर्ट को समझाइए। क्या कोई चिंता की बात है?")
                            }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Ask About Your Report header
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFE0F7FA)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = aiBrainIcon(Cyan),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Spasht AI Insights",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepNavyBlue
                                    )
                                }
                                
                                Text(
                                    text = "Analyzing your reports for instant insights...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MediumGray
                                )
                            }
                        }
                    }

                    // Chat messages rendered as separate items for un-nested layout
                    items(chatMessages) { message ->
                        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                        Row(
                            horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            if (!message.isUser) {
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF00BFA5)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = aiBrainIcon(Color.White),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            
                            Column(
                                modifier = Modifier.weight(1f, fill = false),
                                horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (message.isUser) 12.dp else 0.dp,
                                        bottomEnd = if (message.isUser) 0.dp else 12.dp
                                    ),
                                    color = if (message.isUser) Cyan else Color.White,
                                    border = if (message.isUser) null else BorderStroke(1.dp, Color(0xFFEEEEEE))
                                ) {
                                    Text(
                                        text = message.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (message.isUser) Color.White else DeepNavyBlue,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                                Text(
                                    text = time, // Fallback timestamp as ChatMessage model doesn't have it
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MediumGray
                                )
                            }
                            
                            if (message.isUser) {
                                Spacer(modifier = Modifier.width(12.dp))
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = DeepNavyBlue
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Input field at bottom
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = userMessage,
                                onValueChange = { userMessage = it },
                                placeholder = {
                                    Text(
                                        text = "Ask about your report...",
                                        color = MediumGray
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedBorderColor = Cyan
                                )
                            )
                            
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(24.dp),
                                color = Cyan,
                                onClick = {
                                    if (userMessage.isNotBlank()) {
                                        viewModel.sendFollowUpMessage(userMessage)
                                        userMessage = ""
                                    }
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MediumGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI analysis is for informational purposes only",
                                style = MaterialTheme.typography.bodySmall,
                                color = MediumGray
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Loading dialog with ECG animation
    if (isAnalyzing) {
        Dialog(onDismissRequest = {}) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Cyan
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = aiBrainIcon(Color.White),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = "Analyzing Report",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepNavyBlue
                    )
                    
                    Text(
                        text = "AI is scanning and extracting information...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MediumGray,
                        textAlign = TextAlign.Center
                    )
                    
                    ECGPulseWave(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun UploadedReportCard(
    report: UploadedReport,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(2.dp, Cyan)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF5F5F5)
            ) {
                if (report.isPdf) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = Cyan,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(report.uri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Report thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = report.filename,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepNavyBlue,
                    maxLines = 1
                )
                Text(
                    text = "${String.format("%.1f", report.sizeKB)} KB",
                    style = MaterialTheme.typography.bodySmall,
                    color = MediumGray
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = Color(0xFFEF5350)
                )
            }
        }
    }
}

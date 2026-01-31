package com.ashaai.navigator.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ashaai.navigator.data.model.ChatMessage
import com.ashaai.navigator.ui.ReportAnalysisState
import com.ashaai.navigator.ui.ReportViewModel
import com.ashaai.navigator.utils.PdfUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportChatScreen(
    reportUri: Uri,
    prompt: String = "कृपया इस रिपोर्ट को समझाइए। क्या कोई चिंता की बात है?",
    viewModel: ReportViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val analysisState by viewModel.analysisState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    var showReportPreview by remember { mutableStateOf(true) } // Show by default
    var pdfBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var currentPdfPage by remember { mutableStateOf(0) }
    var totalPdfPages by remember { mutableStateOf(0) }

    // Check if it's a PDF and load first page
    val isPdf = remember(reportUri) {
        context.contentResolver.getType(reportUri) == "application/pdf" ||
        reportUri.toString().endsWith(".pdf")
    }

    // Load PDF page count
    LaunchedEffect(reportUri, isPdf) {
        if (isPdf) {
            withContext(Dispatchers.IO) {
                totalPdfPages = PdfUtils.getPdfPageCount(context, reportUri)
                pdfBitmap = PdfUtils.getPdfPage(context, reportUri, 0)
            }
        }
    }

    // Load specific PDF page when currentPdfPage changes
    LaunchedEffect(currentPdfPage) {
        if (isPdf && currentPdfPage < totalPdfPages) {
            withContext(Dispatchers.IO) {
                pdfBitmap?.recycle()
                pdfBitmap = PdfUtils.getPdfPage(context, reportUri, currentPdfPage)
            }
        }
    }

    // Start analysis when screen loads
    LaunchedEffect(reportUri) {
        if (messages.isEmpty()) {
            viewModel.analyzeReport(reportUri, prompt)
        }
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Analysis") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showReportPreview = !showReportPreview }) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "View Report",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Report preview (collapsible)
            if (showReportPreview) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (isPdf && pdfBitmap != null) {
                                // Show PDF page as bitmap
                                Image(
                                    bitmap = pdfBitmap!!.asImageBitmap(),
                                    contentDescription = "PDF Report Preview Page ${currentPdfPage + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                // Show image directly
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(reportUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Report",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }

                    // PDF page navigation
                    if (isPdf && totalPdfPages > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (currentPdfPage > 0) currentPdfPage-- },
                                enabled = currentPdfPage > 0
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Page")
                            }

                            Text(
                                text = "Page ${currentPdfPage + 1} of $totalPdfPages",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            IconButton(
                                onClick = { if (currentPdfPage < totalPdfPages - 1) currentPdfPage++ },
                                enabled = currentPdfPage < totalPdfPages - 1
                            ) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "Next Page")
                            }
                        }
                    }
                }
            }

            // Messages list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                state = listState
            ) {
                items(messages) { message ->
                    ReportMessageBubble(message)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Loading indicator
                if (analysisState is ReportAnalysisState.Analyzing) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing report...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Input area
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask a follow-up question...") },
                        maxLines = 3,
                        enabled = analysisState !is ReportAnalysisState.Analyzing
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendFollowUpMessage(inputText)
                                inputText = ""
                            }
                        },
                        enabled = inputText.isNotBlank() && analysisState !is ReportAnalysisState.Analyzing
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}

@Composable
fun ReportMessageBubble(message: ChatMessage) {
    val isUser = message.isUser
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isUser)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

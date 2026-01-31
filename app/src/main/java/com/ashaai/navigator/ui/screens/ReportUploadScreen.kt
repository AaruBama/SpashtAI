package com.ashaai.navigator.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ashaai.navigator.utils.PdfUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportUploadScreen(
    onNavigateBack: () -> Unit,
    onReportSelected: (Uri, String) -> Unit
) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var userPrompt by remember { mutableStateOf("कृपया इस रिपोर्ट को समझाइए। क्या कोई चिंता की बात है?") }
    val context = LocalContext.current

    var pdfBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var currentPdfPage by remember { mutableStateOf(0) }
    var totalPdfPages by remember { mutableStateOf(0) }
    var isPdf by remember { mutableStateOf(false) }

    // Load PDF preview when URI changes
    LaunchedEffect(selectedUri) {
        selectedUri?.let { uri ->
            val mimeType = context.contentResolver.getType(uri)
            isPdf = mimeType == "application/pdf" || uri.toString().endsWith(".pdf")

            if (isPdf) {
                withContext(Dispatchers.IO) {
                    totalPdfPages = PdfUtils.getPdfPageCount(context, uri)
                    currentPdfPage = 0
                    pdfBitmap = PdfUtils.getPdfPage(context, uri, 0)
                }
            }
        } ?: run {
            isPdf = false
            pdfBitmap?.recycle()
            pdfBitmap = null
            currentPdfPage = 0
            totalPdfPages = 0
        }
    }

    // Load specific PDF page when currentPdfPage changes
    LaunchedEffect(currentPdfPage, selectedUri) {
        if (isPdf && selectedUri != null && currentPdfPage < totalPdfPages) {
            withContext(Dispatchers.IO) {
                pdfBitmap?.recycle()
                pdfBitmap = PdfUtils.getPdfPage(context, selectedUri!!, currentPdfPage)
            }
        }
    }

    // Image picker - uses OpenDocument for local file system access
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Take persistable URI permission for future access
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            selectedUri = it
        }
    }

    // PDF picker - uses OpenDocument for local file system access
    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Take persistable URI permission for future access
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            selectedUri = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Scanner") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "अपनी मेडिकल रिपोर्ट अपलोड करें",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Select from device storage or cloud",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Upload buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        // Launch file picker for images (supports local storage)
                        imagePicker.launch(arrayOf("image/*"))
                    },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Upload Image",
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Image\nरिपोर्ट", textAlign = TextAlign.Center)
                    }
                }

                OutlinedButton(
                    onClick = {
                        // Launch file picker for PDFs (supports local storage)
                        pdfPicker.launch(arrayOf("application/pdf"))
                    },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = "Upload PDF",
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("PDF\nरिपोर्ट", textAlign = TextAlign.Center)
                    }
                }
            }

            // Preview section
            if (selectedUri != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isPdf) "PDF Preview (${totalPdfPages} pages)" else "Preview",
                    style = MaterialTheme.typography.titleMedium
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    if (isPdf && pdfBitmap != null) {
                        Image(
                            bitmap = pdfBitmap!!.asImageBitmap(),
                            contentDescription = "PDF Preview Page ${currentPdfPage + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(selectedUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Report Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
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

                Spacer(modifier = Modifier.height(16.dp))

                // Prompt input
                OutlinedTextField(
                    value = userPrompt,
                    onValueChange = { userPrompt = it },
                    label = { Text("आपका सवाल (Your Question)") },
                    placeholder = { Text("जैसे: क्या ये रिपोर्ट नॉर्मल है?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Analyze button
                Button(
                    onClick = {
                        selectedUri?.let { uri ->
                            onReportSelected(uri, userPrompt)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedUri != null && userPrompt.isNotBlank()
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze Report", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "📋 Supported formats:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "• Images: JPG, PNG, HEIC\n• Documents: PDF\n\n📁 Select from:\n• Device storage (Downloads, DCIM, Documents)\n• Cloud storage (Google Drive, etc.)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

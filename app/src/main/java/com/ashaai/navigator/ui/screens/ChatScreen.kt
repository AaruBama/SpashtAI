package com.ashaai.navigator.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.media.ToneGenerator
import android.media.AudioManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashaai.navigator.data.model.Message
import com.ashaai.navigator.ui.MainViewModel
import com.ashaai.navigator.ui.theme.Cyan
import com.ashaai.navigator.ui.theme.DeepNavyBlue
import com.ashaai.navigator.ui.theme.MediumGray
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    viewModel: MainViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val voiceError by viewModel.voiceError.collectAsState()
    val voiceText by viewModel.voiceInput.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    var textInput by remember { mutableStateOf("") }
    var lastVoiceText by remember { mutableStateOf("") }
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Populate text input when voice input finishes
    LaunchedEffect(voiceText, isListening) {
        if (!isListening && voiceText.isNotBlank() && voiceText != lastVoiceText) {
            textInput = voiceText
            lastVoiceText = voiceText
        }
    }

    // Show error messages
    LaunchedEffect(voiceError) {
        voiceError?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Stop TTS when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopSpeaking()
        }
    }

    val context = LocalContext.current
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
            viewModel.toggleListening()
        }
    }

    // Media picker for images/files
    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Take persistable URI permission
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            selectedMediaUri = it
            // Add indication to text input
            textInput = if (textInput.isBlank()) {
                "[📎 Media attached]"
            } else {
                "$textInput [📎 Media attached]"
            }
        }
    }

    // Cleanup tone generator
    DisposableEffect(Unit) {
        onDispose {
            toneGenerator.release()
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column {
                            Text(
                                text = "Voice Diagnostic",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "AI-powered health assistant",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Stop button when speaking, otherwise bot icon
                    if (isSpeaking) {
                        IconButton(
                            onClick = { viewModel.stopSpeaking() },
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.error, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeOff,
                                contentDescription = "Stop Speaking",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(DeepNavyBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SmartToy,
                                contentDescription = "AI Assistant",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Chat messages area
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        })
                    },
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    ChatMessageBubble(message)
                }

                // Typing indicator when AI is thinking
                if (isAiThinking) {
                    item {
                        TypingIndicator()
                    }
                }

                // Empty state
                if (messages.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SmartToy,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MediumGray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "नमस्ते! मैं आपका स्वास्थ्य सहायक हूँ। आप\nमुझे अपनी समस्या हिंदी, इंग्लिश या हिंग्लिश\nमें बता सकते हैं। कैसे मदद कर सकता हूँ?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MediumGray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Quick suggestion chips
            SuggestionChips(
                onChipClick = { suggestion ->
                    textInput = suggestion
                }
            )

            // Input area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mic button
                        IconButton(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
                                    viewModel.toggleListening()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (isListening) MaterialTheme.colorScheme.error else DeepNavyBlue,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice input",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Text input field with attachment button inside
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            BasicTextField(
                                value = textInput,
                                onValueChange = { textInput = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isListening)
                                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(24.dp)
                                    )
                                    .padding(start = 16.dp, end = 48.dp, top = 12.dp, bottom = 12.dp),
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                decorationBox = { innerTextField ->
                                    if (textInput.isEmpty()) {
                                        Text(
                                            text = if (isListening)
                                                "Listening... speak now"
                                            else
                                                "Type your symptoms in Hindi or English...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isListening) MaterialTheme.colorScheme.error else MediumGray
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            // Attachment button positioned inside the text field on the right
                            IconButton(
                                onClick = {
                                    mediaPicker.launch(arrayOf("image/*", "application/pdf"))
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 4.dp)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = "Attach media",
                                    tint = MediumGray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Send button
                        IconButton(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    viewModel.sendMessage(textInput)
                                    textInput = ""
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(messages.size)
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Cyan, CircleShape),
                            enabled = textInput.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Disclaimer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SmartToy,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MediumGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI responses are for informational purposes only",
                            style = MaterialTheme.typography.labelSmall,
                            color = MediumGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorBunnyAvatar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .drawBehind {
                val width = size.width
                val height = size.height
                val centerX = width / 2
                val centerY = height / 2

                // Bunny face (white circle)
                drawCircle(
                    color = Color.White,
                    radius = width * 0.35f,
                    center = Offset(centerX, centerY)
                )

                // Left ear
                drawCircle(
                    color = Color.White,
                    radius = width * 0.12f,
                    center = Offset(centerX - width * 0.2f, centerY - height * 0.3f)
                )

                // Right ear
                drawCircle(
                    color = Color.White,
                    radius = width * 0.12f,
                    center = Offset(centerX + width * 0.2f, centerY - height * 0.3f)
                )

                // Left inner ear (pink)
                drawCircle(
                    color = Color(0xFFFFB3BA),
                    radius = width * 0.06f,
                    center = Offset(centerX - width * 0.2f, centerY - height * 0.3f)
                )

                // Right inner ear (pink)
                drawCircle(
                    color = Color(0xFFFFB3BA),
                    radius = width * 0.06f,
                    center = Offset(centerX + width * 0.2f, centerY - height * 0.3f)
                )

                // Left eye
                drawCircle(
                    color = Color(0xFF2C3E50),
                    radius = width * 0.045f,
                    center = Offset(centerX - width * 0.12f, centerY - height * 0.05f)
                )

                // Right eye
                drawCircle(
                    color = Color(0xFF2C3E50),
                    radius = width * 0.045f,
                    center = Offset(centerX + width * 0.12f, centerY - height * 0.05f)
                )

                // Nose (pink triangle)
                val nosePath = Path().apply {
                    moveTo(centerX, centerY + height * 0.05f)
                    lineTo(centerX - width * 0.05f, centerY + height * 0.12f)
                    lineTo(centerX + width * 0.05f, centerY + height * 0.12f)
                    close()
                }
                drawPath(
                    path = nosePath,
                    color = Color(0xFFFFB3BA)
                )

                // Smile (arc)
                val smilePath = Path().apply {
                    moveTo(centerX - width * 0.1f, centerY + height * 0.15f)
                    quadraticBezierTo(
                        centerX, centerY + height * 0.22f,
                        centerX + width * 0.1f, centerY + height * 0.15f
                    )
                }
                drawPath(
                    path = smilePath,
                    color = Color(0xFF2C3E50),
                    style = Stroke(width = width * 0.02f)
                )

                // Doctor's stethoscope (red cross on forehead)
                val crossSize = width * 0.08f
                drawLine(
                    color = Color(0xFFE74C3C),
                    start = Offset(centerX - crossSize / 2, centerY - height * 0.25f),
                    end = Offset(centerX + crossSize / 2, centerY - height * 0.25f),
                    strokeWidth = width * 0.04f
                )
                drawLine(
                    color = Color(0xFFE74C3C),
                    start = Offset(centerX, centerY - height * 0.25f - crossSize / 2),
                    end = Offset(centerX, centerY - height * 0.25f + crossSize / 2),
                    strokeWidth = width * 0.04f
                )
            }
    )
}

@Composable
fun ChatMessageBubble(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            // Bot avatar with gradient and doctor bunny
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4A9FD8),  // Blue
                                Color(0xFFE57373)   // Pink/Red
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                DoctorBunnyAvatar(modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
        ) {
            // Message bubble
            Surface(
                shape = RoundedCornerShape(
                    topStart = if (message.isUser) 16.dp else 4.dp,
                    topEnd = if (message.isUser) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                color = if (message.isUser) DeepNavyBlue.copy(alpha = 0.1f) else Color.White,
                border = if (!message.isUser) BorderStroke(1.dp, Color(0xFFE5E7EB)) else null,
                shadowElevation = if (!message.isUser) 1.dp else 0.dp
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Timestamp
            Text(
                text = formatTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MediumGray,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
            )
        }
    }
}

@Composable
fun SuggestionChips(onChipClick: (String) -> Unit) {
    val suggestions = listOf(
        "मुझे बुखार है",
        "Headache problem",
        "सर्दी खांसी",
        "Stomach pain"
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(suggestions) { suggestion ->
            SuggestionChip(
                text = suggestion,
                onClick = { onChipClick(suggestion) }
            )
        }
    }
}

@Composable
fun SuggestionChip(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Cyan),
        color = Color.Transparent,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Cyan,
            fontWeight = FontWeight.Medium
        )
    }
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun VoicePreviewBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = Alignment.End
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 4.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                color = DeepNavyBlue.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, DeepNavyBlue.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Recording",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
            Text(
                text = "Listening...",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // Bot avatar with gradient and doctor bunny
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF4A9FD8),  // Blue
                            Color(0xFFE57373)   // Pink/Red
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            DoctorBunnyAvatar(modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TypingDot(delay = 0)
                TypingDot(delay = 150)
                TypingDot(delay = 300)
            }
        }
    }
}

@Composable
fun TypingDot(delay: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing, delayMillis = delay),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .background(MediumGray.copy(alpha = alpha), CircleShape)
    )
}

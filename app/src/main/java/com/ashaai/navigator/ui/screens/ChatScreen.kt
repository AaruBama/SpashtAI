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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.ashaai.navigator.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

    var textInput by remember { mutableStateOf("") }
    var lastVoiceText by remember { mutableStateOf("") }
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

    // Cleanup tone generator
    DisposableEffect(Unit) {
        onDispose {
            toneGenerator.release()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.8f),
                shadowElevation = 0.dp
            ) {
                Column {
                    // Top bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Talk to AI",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF10b981), CircleShape)
                                )
                                Text(
                                    text = "AI ASSISTANT ONLINE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Gray500,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { /* More options */ },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreHoriz,
                                contentDescription = "More",
                                tint = Color.Black
                            )
                        }
                    }

                    // Info banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Primary.copy(alpha = 0.05f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Not a substitute for professional medical advice.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Primary
                            )
                        }
                    }
                }
            }

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
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    StitchChatMessage(message)
                }

                // Typing indicator when AI is thinking
                if (isAiThinking) {
                    item {
                        StitchTypingIndicator()
                    }
                }
            }

            // Bottom input area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Quick reply chips
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            listOf(
                                "मुझे बुखार है",
                                "Headache problem",
                                "सर्दी खांसी",
                                "Stomach pain"
                            )
                        ) { suggestion ->
                            StitchQuickReplyChip(
                                text = suggestion,
                                onClick = { textInput = suggestion }
                            )
                        }
                    }

                    // Input field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Add button
                        IconButton(
                            onClick = { /* Attach */ },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFf3f4f6), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Attach",
                                tint = Gray500,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Text input
                        BasicTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFf3f4f6), RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Black
                            ),
                            cursorBrush = SolidColor(Primary),
                            decorationBox = { innerTextField ->
                                if (textInput.isEmpty()) {
                                    Text(
                                        text = "Describe how you're feeling...",
                                        fontSize = 14.sp,
                                        color = Gray500
                                    )
                                }
                                innerTextField()
                            }
                        )

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
                                .size(40.dp)
                                .background(Color(0xFFf3f4f6), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = "Voice",
                                tint = Gray500,
                                modifier = Modifier.size(24.dp)
                            )
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
                                .size(40.dp)
                                .background(Primary, CircleShape),
                            enabled = textInput.isNotBlank()
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun StitchChatMessage(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            // AI avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Emergency,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
        ) {
            // Label
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (message.isUser) "You" else "AI Health Assistant",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray400
                )
                if (message.isUser) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Play",
                        tint = Gray400,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Message bubble
            Surface(
                shape = RoundedCornerShape(
                    topStart = if (message.isUser) 16.dp else 16.dp,
                    topEnd = if (message.isUser) 16.dp else 16.dp,
                    bottomStart = if (message.isUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 16.dp
                ),
                color = if (message.isUser) Primary else Color(0xFFf0f2f4),
                shadowElevation = if (!message.isUser) 1.dp else 4.dp
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(16.dp, 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = if (message.isUser) Color.White else Color.Black
                )
            }
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(12.dp))
            // User avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFFe5e7eb), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Gray500,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun StitchTypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // AI avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Emergency,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "AI Health Assistant",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray400,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TypingDot(delay = 0)
                    TypingDot(delay = 200)
                    TypingDot(delay = 400)
                }
                Text(
                    text = "Analyzing symptoms...",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray400,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
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
            .size(6.dp)
            .background(Color(0xFFd1d5db).copy(alpha = alpha), CircleShape)
    )
}

@Composable
fun StitchQuickReplyChip(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFe5e7eb)),
        color = Color.White,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

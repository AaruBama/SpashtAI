package com.ashaai.navigator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashaai.navigator.ui.AnalysisState
import com.ashaai.navigator.ui.MainViewModel
import com.ashaai.navigator.ui.components.ActionCard
import com.ashaai.navigator.ui.components.PatientInsightCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboard(
    viewModel: MainViewModel = viewModel(),
    onNavigateToSettings: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToReportScanner: () -> Unit = {}
) {
    val analysisState by viewModel.analysisState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spasht") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Diagnostic Intelligence",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select a diagnostic tool to begin",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Grid of Action Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ActionCard(
                        title = "Voice Diagnostic",
                        description = "Record symptoms (Hinglish)",
                        icon = Icons.Outlined.Mic,
                        containerColor = Color(0xFF4A7C96), // Dark teal blue
                        onClick = onNavigateToChat
                    )
                }
                item {
                    ActionCard(
                        title = "Report Scanner",
                        description = "Scan medical reports",
                        icon = Icons.Outlined.Description,
                        containerColor = Color(0xFF70D4E8), // Light cyan blue
                        onClick = onNavigateToReportScanner
                    )
                }
                item {
                    ActionCard(
                        title = "Acoustic Check",
                        description = "Analyze lung sounds",
                        icon = Icons.Outlined.GraphicEq,
                        containerColor = Color(0xFF6BB6A1), // Teal green
                        onClick = { /* TODO: Launch HeAR */ }
                    )
                }
                item {
                    ActionCard(
                        title = "More Options",
                        description = "Coming soon",
                        icon = Icons.Outlined.MoreHoriz,
                        containerColor = Color(0xFFE8E8E8), // Light gray
                        onClick = { /* TODO: More options */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Display Results if available
            when (val state = analysisState) {
                is AnalysisState.Success -> {
                    PatientInsightCard(
                         medicalTerm = state.data.findings,
                         simplifiedText = state.data.simplified_text,
                         isLoading = false
                    )
                }
                is AnalysisState.Loading -> {
                    PatientInsightCard(
                        medicalTerm = "Analyzing...",
                        simplifiedText = "कृपया प्रतीक्षा करें...",
                        isLoading = true
                    )
                }
                is AnalysisState.Error -> {
                    PatientInsightCard(
                        medicalTerm = "Error: ${state.message}",
                        simplifiedText = "त्रुटि हुई",
                        isLoading = false
                    )
                }
                else -> {
                    // Idle state or simplified 'Spasht' View placeholder
                    PatientInsightCard(
                        medicalTerm = "No Active Analysis",
                        simplifiedText = "कोई सक्रिय रिपोर्ट नहीं",
                        isLoading = false
                    )
                }
            }
        }
    }
}

package com.ashaai.navigator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
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
import com.ashaai.navigator.ui.components.icons.*
import com.ashaai.navigator.ui.theme.AshaAIGradients
import com.ashaai.navigator.ui.theme.Cyan

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
            // Minimal header matching reference design
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Spasht",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Diagnostic Intelligence",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select a diagnostic method to begin",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Action Cards in a 2-column grid
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionCard(
                            title = "Voice Diagnostic",
                            description = "Record symptoms\n(Hinglish)",
                            icon = voiceDiagnosisIcon(Color.White),
                            brush = AshaAIGradients.voiceDiagnosticBrush,
                            onClick = onNavigateToChat,
                            modifier = Modifier.weight(1f)
                        )
                        ActionCard(
                            title = "Report Scanner",
                            description = "Scan medical reports",
                            icon = uploadReportIcon(Color.White),
                            brush = AshaAIGradients.reportScannerBrush,
                            onClick = onNavigateToReportScanner,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionCard(
                            title = "Acoustic Check",
                            description = "Analyze lung sounds",
                            icon = acousticDiagnosisIcon(Color.White),
                            brush = AshaAIGradients.acousticCheckBrush,
                            onClick = { /* TODO: Launch HeAR */ },
                            modifier = Modifier.weight(1f)
                        )
                        ActionCard(
                            title = "More Options",
                            description = "Coming soon",
                            icon = Icons.Outlined.MoreHoriz,
                            brush = AshaAIGradients.moreOptionsBrush,
                            onClick = { /* TODO: More options */ },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Display Results if available
            item {
                when (val state = analysisState) {
                    is AnalysisState.Success -> {
                        PatientInsightCard(
                            medicalTerm = state.data.findings,
                            simplifiedText = state.data.simplified_text,
                            hasData = true,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    is AnalysisState.Loading -> {
                        PatientInsightCard(
                            medicalTerm = "Analyzing...",
                            simplifiedText = "कृपया प्रतीक्षा करें...",
                            hasData = true,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    is AnalysisState.Error -> {
                        PatientInsightCard(
                            medicalTerm = "Error: ${state.message}",
                            simplifiedText = "त्रुटि हुई",
                            hasData = true,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    else -> {
                        // Idle state or simplified 'Spasht' View placeholder
                        PatientInsightCard(
                            hasData = false,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

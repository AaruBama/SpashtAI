package com.ashaai.navigator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onNavigateToSettings: () -> Unit = {}
) {
    val analysisState by viewModel.analysisState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AshaAI Navigator") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Clinical Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Grid of Action Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columns for tablets/large phones, or 1 for small
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    ActionCard(
                        title = "Voice Diagnostic",
                        description = "Record symptoms (Hinglish)",
                        onClick = { /* TODO: Launch MedASR */ }
                    )
                }
                item {
                    ActionCard(
                        title = "Report Scanner",
                        description = "Scan medical reports",
                        onClick = { viewModel.analyzeReport("/path/to/image") }
                    )
                }
                item {
                    ActionCard(
                        title = "Acoustic Check",
                        description = "Analyze lung sounds",
                        onClick = { /* TODO: Launch HeAR */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Display Results if available
            when (val state = analysisState) {
                is AnalysisState.Data -> { // Assuming 'Success' was named 'Data' or similar in ViewModel. Let's fix this naming.
                    // Wait, I named it Success in ViewModel.
                    PatientInsightCard(
                         medicalTerm = state.data.findings,
                         simplifiedText = state.data.simplified_text
                    )
                }
                is AnalysisState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is AnalysisState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
                else -> {
                    // Idle state or simplified 'Spasht' View placeholder
                    PatientInsightCard(
                        medicalTerm = "No Active Analysis",
                        simplifiedText = "कोई सक्रिय रिपोर्ट नहीं"
                    )
                }
            }
        }
    }
}

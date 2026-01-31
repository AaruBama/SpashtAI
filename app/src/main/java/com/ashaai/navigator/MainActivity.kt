package com.ashaai.navigator

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ashaai.navigator.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AshaAINavigatorApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AshaAINavigatorApp() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                val items = listOf("Home", "History", "Settings")
                val icons = listOf(Icons.Default.Home, Icons.Default.History, Icons.Default.Settings)
                
                items.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = screen) },
                        label = { Text(screen) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen } == true,
                        onClick = {
                            navController.navigate(screen) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "Home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("Home") {
                HomeDashboard(
                    onNavigateToSettings = { navController.navigate("Settings") },
                    onNavigateToChat = { navController.navigate("Chat") },
                    onNavigateToReportScanner = { navController.navigate("ReportUpload") }
                )
            }
            composable("Chat") {
                ChatScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("ReportUpload") {
                ReportUploadScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onReportSelected = { uri, prompt ->
                        // Encode URI to pass as navigation argument
                        val encodedUri = Uri.encode(uri.toString())
                        navController.navigate("ReportChat/$encodedUri/$prompt")
                    }
                )
            }
            composable(
                route = "ReportChat/{reportUri}/{prompt}",
                arguments = listOf(
                    navArgument("reportUri") { type = NavType.StringType },
                    navArgument("prompt") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val uriString = backStackEntry.arguments?.getString("reportUri")
                val prompt = backStackEntry.arguments?.getString("prompt")

                if (uriString != null && prompt != null) {
                    val uri = Uri.parse(uriString)
                    ReportChatScreen(
                        reportUri = uri,
                        prompt = prompt,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
            composable("History") {
                HistoryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onReportClick = { reportId ->
                        navController.navigate("ReportHistory/$reportId")
                    }
                )
            }
            composable(
                route = "ReportHistory/{reportId}",
                arguments = listOf(navArgument("reportId") { type = NavType.LongType })
            ) { backStackEntry ->
                val reportId = backStackEntry.arguments?.getLong("reportId") ?: 0L
                // Load report from history and show in chat
                Text("Report History Detail: $reportId")
            }
            composable("Settings") { Text("Settings Screen Placeholder") }
        }
    }
}

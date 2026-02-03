package com.ashaai.navigator

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ashaai.navigator.ui.components.icons.medicalRecordsIcon
import com.ashaai.navigator.ui.screens.*
import com.ashaai.navigator.ui.theme.DeepNavyBlue
import com.ashaai.navigator.ui.theme.MediumGray

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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    
                    val items = listOf("Home", "History", "Settings")
                    val icons: List<ImageVector> = listOf(
                        Icons.Outlined.Home,
                        medicalRecordsIcon(MediumGray),
                        Icons.Outlined.Settings
                    )
                    
                    items.forEachIndexed { index, screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen } == true
                        
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    navController.navigate(screen) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        color = if (isSelected) DeepNavyBlue else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icons[index],
                                    contentDescription = screen,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (isSelected) Color.White else MediumGray
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = screen,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (isSelected) DeepNavyBlue else MediumGray
                            )
                        }
                    }
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
                ReportScannerScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onReportAnalyzed = { uri, prompt ->
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
                Text("Report History Detail: $reportId")
            }
            composable("Settings") { Text("Settings Screen Placeholder") }
        }
    }
}

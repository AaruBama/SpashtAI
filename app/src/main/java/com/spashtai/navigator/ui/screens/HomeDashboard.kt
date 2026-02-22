package com.spashtai.navigator.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spashtai.navigator.ui.MainViewModel
import com.spashtai.navigator.ui.components.*
import com.spashtai.navigator.ui.models.HealthTip
import com.spashtai.navigator.ui.theme.*

@Composable
fun HomeDashboard(
    viewModel: MainViewModel = viewModel(),
    onNavigateToSettings: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToReportScanner: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Primary, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Spasht AI",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }

                    // Profile picture
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .border(2.dp, Primary.copy(alpha = 0.2f), CircleShape)
                            .padding(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Primary,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }
                }
            }

            // Greeting
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Good morning, Alex",
                        fontSize = 14.sp,
                        color = Gray500
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ready for your check-up?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 30.sp
                    )
                }
            }

            // Feature Cards (2x2 Grid)
            item {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionCard(
                            title = "AI Symptom Checker",
                            description = "Talk to AI",
                            icon = Icons.Default.Mic,
                            onClick = onNavigateToChat,
                            modifier = Modifier.weight(1f),
                            backgroundColor = BlueCardBg,
                            iconBackgroundColor = BlueIconBg,
                            iconTint = Primary,
                            borderColor = CardBorder
                        )
                        ActionCard(
                            title = "X-rays, CT & Reports",
                            description = "Analyze",
                            icon = Icons.Outlined.FolderOpen,
                            onClick = onNavigateToReportScanner,
                            modifier = Modifier.weight(1f),
                            backgroundColor = TealCardBg,
                            iconBackgroundColor = TealIconBg,
                            iconTint = TealIcon,
                            borderColor = CardBorder
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionCard(
                            title = "Cough & Breathing",
                            description = "Voice Analysis",
                            icon = Icons.Default.GraphicEq,
                            onClick = { /* TODO */ },
                            modifier = Modifier.weight(1f),
                            backgroundColor = PurpleCardBg,
                            iconBackgroundColor = PurpleIconBg,
                            iconTint = PurpleIcon,
                            borderColor = CardBorder
                        )
                        ActionCard(
                            title = "My Health History",
                            description = "Journey",
                            icon = Icons.Default.MonitorHeart,
                            onClick = { /* TODO */ },
                            modifier = Modifier.weight(1f),
                            backgroundColor = OrangeCardBg,
                            iconBackgroundColor = OrangeIconBg,
                            iconTint = OrangeIcon,
                            borderColor = CardBorder
                        )
                    }
                }
            }

            // Daily Health Tips
            item {
                Column(
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Daily Health Tips",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            HealthTipCard(
                                icon = Icons.Default.WaterDrop,
                                title = "Stay hydrated for better digestion",
                                subtitle = "Target: 2.5L today",
                                gradient = Brush.linearGradient(
                                    colors = listOf(Primary, Color(0xFF60a5fa))
                                )
                            )
                        }
                        item {
                            HealthTipCard(
                                icon = Icons.Default.DirectionsWalk,
                                title = "Take 10,000 steps today",
                                subtitle = "You're at 4,500 now",
                                gradient = Brush.linearGradient(
                                    colors = listOf(TealGradientStart, TealGradientEnd)
                                )
                            )
                        }
                    }
                }
            }

            // Your Summary
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = "Your Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    HealthSummaryCard(
                        score = 85,
                        scoreLabel = "Excellent",
                        nextStepTitle = "Next Step",
                        nextStepDescription = "Schedule your annual check-up to keep your score high.",
                        onBookAppointment = { /* TODO */ }
                    )
                }
            }
        }
    }
}

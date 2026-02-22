package com.spashtai.navigator.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Primary Colors (Trust & Professionalism)
val DeepNavyBlue = Color(0xFF0A4D68)
val LightBlue = Color(0xFF0D6A92)

// Secondary Colors (Technology)
val Cyan = Color(0xFF00D9FF)

// Accent Colors (Health & Status)
val EmeraldGreen = Color(0xFF10B981)
val Orange = Color(0xFFF59E0B)
val Red = Color(0xFFEF4444)
val Purple = Color(0xFF8B5CF6)

// Additional UI Colors
val LightGray = Color(0xFFF5F5F5)
val MediumGray = Color(0xFF9CA3AF)
val DarkGray = Color(0xFF374151)

val LightColorScheme = lightColorScheme(
    primary = DeepNavyBlue,
    onPrimary = Color.White,
    primaryContainer = LightBlue,
    onPrimaryContainer = Color.White,
    
    secondary = Cyan,
    onSecondary = Color.White,
    secondaryContainer = Cyan.copy(alpha = 0.1f),
    onSecondaryContainer = DeepNavyBlue,
    
    tertiary = EmeraldGreen,
    onTertiary = Color.White,
    tertiaryContainer = EmeraldGreen.copy(alpha = 0.1f),
    onTertiaryContainer = DarkGray,
    
    error = Red,
    onError = Color.White,
    errorContainer = Red.copy(alpha = 0.1f),
    onErrorContainer = Red,
    
    background = Color.White,
    onBackground = DarkGray,
    
    surface = Color.White,
    onSurface = DarkGray,
    surfaceVariant = LightGray,
    onSurfaceVariant = MediumGray,
    
    outline = MediumGray,
    outlineVariant = LightGray
)

val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    onPrimary = Color.White,
    primaryContainer = DeepNavyBlue,
    onPrimaryContainer = Color.White,
    
    secondary = Cyan,
    onSecondary = DarkGray,
    secondaryContainer = Cyan.copy(alpha = 0.2f),
    onSecondaryContainer = Color.White,
    
    tertiary = EmeraldGreen,
    onTertiary = Color.White,
    tertiaryContainer = EmeraldGreen.copy(alpha = 0.2f),
    onTertiaryContainer = Color.White,
    
    error = Red,
    onError = Color.White,
    errorContainer = Red.copy(alpha = 0.2f),
    onErrorContainer = Color.White,
    
    background = Color(0xFF1F2937),
    onBackground = Color.White,
    
    surface = Color(0xFF1F2937),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF374151),
    onSurfaceVariant = MediumGray,
    
    outline = MediumGray,
    outlineVariant = DarkGray
)

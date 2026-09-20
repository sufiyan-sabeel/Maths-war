package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MathBrawlColorScheme = darkColorScheme(
    primary = Color(0xFF00E5FF),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004D40),
    secondary = Color(0xFFFF9100),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFD600),
    background = Color(0xFF090A0E),
    onBackground = Color.White,
    surface = Color(0xFF13141F),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1C1D2A),
    onSurfaceVariant = Color(0xFFB0B0C0)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = MathBrawlColorScheme,
        typography = Typography,
        content = content
    )
}


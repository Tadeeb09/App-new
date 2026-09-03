package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

val EvoroColorScheme = darkColorScheme(
    primary = EvoroWhite,
    onPrimary = EvoroBlack,
    primaryContainer = EvoroSurface2,
    onPrimaryContainer = EvoroWhite,
    secondary = EvoroTextSecondary,
    onSecondary = EvoroWhite,
    secondaryContainer = EvoroSurface1,
    onSecondaryContainer = EvoroWhite,
    tertiary = EvoroTextMuted,
    onTertiary = EvoroWhite,
    background = EvoroBlack,
    onBackground = EvoroWhite,
    surface = EvoroSurface0,
    onSurface = EvoroWhite,
    surfaceVariant = EvoroSurface2,
    onSurfaceVariant = EvoroTextSecondary,
    outline = EvoroBorder,
    outlineVariant = EvoroBorderLight,
    surfaceContainer = EvoroSurface1,
    surfaceContainerHigh = EvoroSurface2,
    surfaceContainerHighest = EvoroSurface3
)

@Composable
fun EvoroTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EvoroColorScheme,
        typography = Typography,
        content = content
    )
}

// Retain alias for any existing template references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    EvoroTheme(content = content)
}

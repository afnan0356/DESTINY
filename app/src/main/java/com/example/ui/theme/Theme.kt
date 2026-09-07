package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DestinyPrimary,
    onPrimary = DestinyOnPrimary,
    primaryContainer = DestinyPrimaryContainer,
    onPrimaryContainer = DestinyOnPrimaryContainer,
    secondary = DestinySecondary,
    onSecondary = DestinyOnSecondary,
    secondaryContainer = DestinySecondaryContainer,
    onSecondaryContainer = DestinyOnSecondaryContainer,
    tertiary = DestinySuccess,
    onTertiary = DestinyOnSuccess,
    tertiaryContainer = DestinySuccessContainer,
    onTertiaryContainer = DestinyOnSuccessContainer,
    background = DestinyBg,
    onBackground = DestinyTextPrimary,
    surface = DestinySurface,
    onSurface = DestinyTextPrimary,
    surfaceVariant = DestinySurfaceVariant,
    onSurfaceVariant = DestinyTextSecondary,
    outline = DestinyOutline,
    outlineVariant = DestinyOutlineVariant
)

@Composable
fun DestinyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

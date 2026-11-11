package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BlackGoldColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = LuxuryBlack,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = GoldSecondary,
    onSecondary = LuxuryBlack,
    secondaryContainer = LuxurySurfaceVariant,
    onSecondaryContainer = GoldLight,
    tertiary = GoldDark,
    onTertiary = TextWhite,
    background = LuxuryDarkBg,
    onBackground = TextWhite,
    surface = LuxurySurface,
    onSurface = TextWhite,
    surfaceVariant = LuxurySurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = LuxuryBorderBright,
    error = RedAlert,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BlackGoldColorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OpenShopVibrantColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = OpenGreen,
    onSecondary = Color.White,
    secondaryContainer = OpenGreenBg,
    onSecondaryContainer = OpenGreen,
    tertiary = BreakAmber,
    onTertiary = Color.White,
    tertiaryContainer = BreakAmberBg,
    onTertiaryContainer = BreakAmber,
    background = ObsidianDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    error = ClosedRed,
    errorContainer = ClosedRedBg,
    onError = Color.White
)

@Composable
fun OpenShopTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OpenShopVibrantColorScheme,
        typography = Typography,
        content = content
    )
}


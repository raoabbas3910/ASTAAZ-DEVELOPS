package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NwsYellow,
    onPrimary = NwsNavyDark,
    primaryContainer = NwsNavyMedium,
    onPrimaryContainer = NwsYellow,
    secondary = NwsCyan,
    onSecondary = Color.Black,
    tertiary = NwsGreen,
    background = NwsNavyDark,
    onBackground = TextPrimaryLight,
    surface = NwsNavyMedium,
    onSurface = TextPrimaryLight,
    surfaceVariant = NwsNavyLight,
    onSurfaceVariant = TextSecondaryLight,
    error = NwsRedSos,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = NwsYellowDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    tertiary = NwsGreen,
    background = Color(0xFFF8FAFC),
    onBackground = TextPrimaryDark,
    surface = Color.White,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryDark,
    error = NwsRedSos,
    onError = Color.White
)

private val HighContrastColorScheme = darkColorScheme(
    primary = HighContrastYellow,
    onPrimary = Color.Black,
    primaryContainer = HighContrastYellow,
    onPrimaryContainer = Color.Black,
    secondary = Color.White,
    onSecondary = Color.Black,
    tertiary = HighContrastYellow,
    background = HighContrastBg,
    onBackground = HighContrastWhite,
    surface = HighContrastSurface,
    onSurface = HighContrastWhite,
    surfaceVariant = Color(0xFF222222),
    onSurfaceVariant = HighContrastWhite,
    error = Color(0xFFFF3333),
    onError = Color.White
)

@Composable
fun TravelNwsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrastMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrastMode -> HighContrastColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

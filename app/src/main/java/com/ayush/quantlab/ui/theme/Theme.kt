package com.ayush.quantlab.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = QuantBlue,
    onPrimary = QuantTextPrimary,
    secondary = QuantBlueSoft,
    onSecondary = QuantBackground,
    background = QuantBackground,
    onBackground = QuantTextPrimary,
    surface = QuantSurface,
    onSurface = QuantTextPrimary,
    surfaceVariant = QuantSurfaceElevated,
    onSurfaceVariant = QuantTextSecondary,
    outline = QuantBorder
)

@Composable
fun QuantLabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = QuantTypography,
        content = content
    )
}

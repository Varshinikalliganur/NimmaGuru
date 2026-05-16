package com.nimmaguru.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = LeafGreen,
    onPrimary = Color.White,
    primaryContainer = MintSurface,
    onPrimaryContainer = DeepGreen,
    secondary = ForestGreen,
    onSecondary = Color.White,
    tertiary = AccentLime,
    onTertiary = DeepGreen,
    background = SoftCream,
    onBackground = DeepGreen,
    surface = Color.White,
    onSurface = DeepGreen,
    surfaceVariant = MintSurface,
    onSurfaceVariant = DeepGreen,
    outline = LeafGreen,
)

private val DarkColors = darkColorScheme(
    primary = AccentLime,
    onPrimary = DeepGreen,
    primaryContainer = ForestGreen,
    onPrimaryContainer = SoftCream,
    secondary = MintSurface,
    onSecondary = DeepGreen,
    tertiary = LeafGreen,
    onTertiary = Color.White,
    background = DeepGreen,
    onBackground = SoftCream,
    surface = ForestGreen,
    onSurface = SoftCream,
    surfaceVariant = LeafGreen,
    onSurfaceVariant = SoftCream,
    outline = AccentLime,
)

@Composable
fun NimmaGuruTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = NimmaTypography,
        content = content,
    )
}

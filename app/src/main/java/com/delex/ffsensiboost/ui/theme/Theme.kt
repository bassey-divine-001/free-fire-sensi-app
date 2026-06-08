package com.delex.ffsensiboost.ui.theme

import androidx.compose.foundation.isSystemInDarkMode
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryNeon,
    onPrimary = PrimaryDark,
    primaryContainer = SurfaceVariant,
    onPrimaryContainer = PrimaryNeon,
    
    secondary = SecondaryNeon,
    onSecondary = PrimaryDark,
    secondaryContainer = SurfaceVariant,
    onSecondaryContainer = SecondaryNeon,
    
    tertiary = TertiaryNeon,
    onTertiary = PrimaryDark,
    tertiaryContainer = SurfaceVariant,
    onTertiaryContainer = TertiaryNeon,
    
    background = BackgroundDark,
    onBackground = TextPrimary,
    
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    
    outline = DividerColor,
    outlineVariant = DividerColor,
    
    error = ErrorRed,
    onError = PrimaryDark,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = ErrorRed,
    
    scrim = Color(0xFF000000).copy(alpha = 0.6f)
)

@Composable
fun FFSensiBoostTheme(
    darkTheme: Boolean = isSystemInDarkMode(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

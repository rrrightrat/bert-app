package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NetflixRed,
    onPrimary = Color.White,
    primaryContainer = NetflixDarkRed,
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = NetflixOffWhite,
    onSecondary = Color.Black,
    secondaryContainer = NetflixDarkSurfaceVariant,
    onSecondaryContainer = NetflixWhite,
    tertiary = AccentAmber,
    onTertiary = Color.Black,
    background = NetflixBlack,
    onBackground = NetflixWhite,
    surface = NetflixDarkSurface,
    onSurface = NetflixWhite,
    surfaceVariant = NetflixDarkSurfaceVariant,
    onSurfaceVariant = NetflixGray,
    outline = NetflixCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = NetflixRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEBEE),
    onPrimaryContainer = NetflixDarkRed,
    secondary = Color(0xFF1E293B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = Color(0xFF0F172A),
    tertiary = AccentAmber,
    onTertiary = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF111111),
    surface = Color.White,
    onSurface = Color(0xFF111111),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF4B5563),
    outline = Color(0xFFE5E7EB)
)

@Composable
fun BERTTheme(
    darkTheme: Boolean = true, // Default to Netflix Dark
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

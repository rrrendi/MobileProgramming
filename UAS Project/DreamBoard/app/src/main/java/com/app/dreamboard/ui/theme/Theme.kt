package com.app.dreamboard.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Skema Warna Gelap (Nuansa Malam/Luar Angkasa)
private val DarkColorScheme = darkColorScheme(
    primary = DreamBluePrimary,
    secondary = DreamBlueSecondary,
    tertiary = DreamBlueTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkText,
    onSurface = DarkText
)

// Skema Warna Terang (Nuansa Awan/Langit Siang)
private val LightColorScheme = lightColorScheme(
    primary = DreamBluePrimary,
    secondary = DreamBlueSecondary,
    tertiary = DreamBlueTertiary,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightText,
    onSurface = LightText
)

@Composable
fun DreamBoardTheme(
    darkTheme: Boolean, // Kontrol manual dari Switch
    dynamicColor: Boolean = false, // Matikan agar warna biru kita tidak ditimpa Android
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
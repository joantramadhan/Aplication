package com.joant.kampussmart.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- SKEMA WARNA DARK MODE (GELAP) ---
private val DarkColorScheme = darkColorScheme(
    primary = MainBlue,
    onPrimary = White,
    primaryContainer = DarkBlue,
    onPrimaryContainer = White,

    background = Black,     // Layar Hitam
    onBackground = White,
    surface = TextBlack,
    onSurface = White,

    // --- KUNCI: KARTU DI MODE GELAP = ABU GELAP ---
    surfaceVariant = Color(0xFF252525),
    onSurfaceVariant = Color(0xFFB0B0B0),

    outline = IconGray
)

// --- SKEMA WARNA LIGHT MODE (TERANG) ---
private val LightColorScheme = lightColorScheme(
    primary = MainBlue,
    onPrimary = White,
    primaryContainer = IconBlueBg,
    onPrimaryContainer = DarkBlue,

    background = BackgroundGray, // Layar Abu Terang
    onBackground = TextBlack,
    surface = White,
    onSurface = TextBlack,

    // --- KUNCI: KARTU DI MODE TERANG = PUTIH BERSIH ---
    surfaceVariant = White,
    onSurfaceVariant = TextGray,

    outline = ProgressBarTrack
)

@Composable
fun KampusSmartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
package com.joant.kampussmart.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.joant.kampussmart.SettingsStorage

// Definisikan Warna
private val DarkColorScheme = darkColorScheme(
    primary = MainBlue,
    secondary = CardCyan,
    background = Color(0xFF121212), // Hitam pekat
    surface = Color(0xFF1E1E1E),    // Abu tua untuk Card
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = MainBlue,
    secondary = CardCyan,
    background = BackgroundGray,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun KampusSmartTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    // Cek Storage: Apakah user minta Dark Mode?
    val isDarkMode = SettingsStorage.isDarkMode(context)

    val colorScheme = if (isDarkMode) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Pastikan Typography.kt ada, atau hapus baris ini
        content = content
    )
}
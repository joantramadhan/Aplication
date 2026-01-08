package com.joant.kampussmart.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// --- WARNA UTAMA & GRADASI ---
val MainBlue = Color(0xFF3070F6)
val LightBlue = Color(0xFF4294FC)
val LightBlueHeader = Color(0xFF4294FC)
val DarkBlue = Color(0xFF1E4598)
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

// --- WARNA NETRAL & UI ---
val BackgroundGray = Color(0xFFF8F9FA)
val LightGrayBackground = Color(0xFFE6E9EF)
val TextBlack = Color(0xFF1F2937)
val TextGray = Color(0xFF6B7280)
val IconGray = Color(0xFF90A4AE)
val IconBlueBg = Color(0xFFE0F2FE)
val ProgressBarTrack = Color(0xFFE5E7EB)
val RedDelete = Color(0xFFEF5350)

// --- WARNA KARTU JADWAL ---
val TimelineBlue = Color(0xFF90CAF9)
val TimelineGreen = Color(0xFFA5D6A7)
val TimelineYellow = Color(0xFFFFF59D)
val CardBlue = Color(0xFF8BBEF1)
val CardCyan = Color(0xFF76DAD5)

// --- GRADASI ---
val BlueGradient = Brush.horizontalGradient(
    colors = listOf(LightBlue, MainBlue)
)

val DarkBlueGradient = Color(0xFF0D47A1)

val VerticalBlueGradient = Brush.verticalGradient(
    colors = listOf(LightBlueHeader, MainBlue)
)

// SUDAH BERSIH: Tidak ada lagi Purple80, Purple40, atau Pink80.
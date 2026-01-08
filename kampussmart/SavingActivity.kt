package com.joant.kampussmart

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joant.kampussmart.ui.theme.MainBlue
import com.joant.kampussmart.ui.theme.White
// Pastikan DarkBlueGradient ada di Color.kt, jika error ganti jadi MainBlue
import com.joant.kampussmart.ui.theme.DarkBlueGradient
import kotlinx.coroutines.delay

class SavingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. TANGKAP DATA DARI RESULT
        // Menggunakan ArrayList agar sesuai dengan pengiriman dari ResultActivity
        val finalData = intent.getParcelableArrayListExtra<ScheduleItem>("FINAL_DATA")

        setContent {
            SavingScreen(
                onSavingComplete = {
                    // 2. SIMPAN KE STORAGE PERMANEN
                    // Gunakan 'this@SavingActivity' agar Context terbaca jelas
                    if (finalData != null) {
                        ScheduleStorage.saveSchedule(this@SavingActivity, finalData)
                    }

                    // 3. PINDAH KE DASHBOARD
                    val intent = Intent(this@SavingActivity, DashboardActivity::class.java).apply {
                        // Flag ini mencegah user kembali ke halaman Loading saat tekan Back
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            )
        }
    }
}

@Composable
fun SavingScreen(onSavingComplete: () -> Unit) {

    // Jika DarkBlueGradient error, ganti colors = listOf(MainBlue, MainBlue)
    val gradientBrush = remember {
        Brush.verticalGradient(
            colors = listOf(DarkBlueGradient, MainBlue),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )
    }

    // Simulasi loading 2 detik sebelum pindah
    LaunchedEffect(Unit) {
        delay(2000L)
        onSavingComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = White,
                strokeWidth = 6.dp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Menyimpan...",
                fontSize = 24.sp,
                color = White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Jadwal kamu sedang disusun",
                fontSize = 16.sp,
                color = White.copy(alpha = 0.8f)
            )
        }
    }
}
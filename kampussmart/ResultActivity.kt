package com.joant.kampussmart

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joant.kampussmart.ui.theme.*

// Pastikan turunannya BaseActivity
class ResultActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scheduleList = intent.getParcelableArrayListExtra<ScheduleItem>("SCHEDULE_LIST") ?: arrayListOf()

        setContent {
            // Bungkus dengan Tema
            KampusSmartTheme {
                ResultScreen(
                    scheduleList = scheduleList,
                    onBackClick = { finish() },
                    onSaveClick = {
                        val intent = Intent(this, SavingActivity::class.java).apply {
                            putParcelableArrayListExtra("FINAL_DATA", ArrayList(scheduleList))
                        }
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun ResultScreen(scheduleList: List<ScheduleItem>, onBackClick: () -> Unit, onSaveClick: () -> Unit) {

    // --- 1. LOGIKA DETEKSI TEMA (KONSISTEN) ---
    // Cek apakah teks utama warnanya Putih? (Indikator Dark Mode)
    val isAppInDarkMode = MaterialTheme.colorScheme.onBackground == White

    // --- 2. TENTUKAN WARNA KARTU ---
    val cardColor = if (isAppInDarkMode) {
        Color(0xFF252525) // Dark Mode: Abu Gelap
    } else {
        White // Light Mode: Putih Bersih
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // BACKGROUND: Otomatis ikut Tema (Hitam/Putih)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // --- Header ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                // Icon Back warnanya menyesuaikan background
                Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground)
            }
            Text(
                text = "Konfirmasi Hasil Scan",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp),
                // Judul Halaman: Hitam (Light) / Putih (Dark)
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(Modifier.height(16.dp))

        // --- List Hasil ---
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(scheduleList) { item ->
                Card(
                    // WARNA KARTU: Menggunakan logika cerdas di atas
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {

                        // 1. Matakuliah: Gunakan MainBlue (Biru Utama) agar cantik & konsisten
                        Text(
                            text = "📚 ${item.subject}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MainBlue
                        )
                        Spacer(Modifier.height(8.dp))

                        // 2. Dosen: Warna Teks Standar (Hitam/Putih)
                        Text(
                            text = "👨‍🏫 ${item.lecturer}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // 3. Ruang
                        Text(
                            text = "📍 ${item.room}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // 4. Tanggal/Waktu: Warna agak pudar (Abu-abu)
                        Text(
                            text = "📅 ${item.time}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Tombol Simpan
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            // TOMBOL: Tetap Biru (Primary) di kedua mode
            colors = ButtonDefaults.buttonColors(containerColor = MainBlue)
        ) {
            // TEKS TOMBOL: Putih
            Text("Simpan Semua", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
        }
    }
}
package com.joant.kampussmart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joant.kampussmart.ui.theme.*

@Composable
fun CalendarScreen(schedule: List<ScheduleItem>) {

    // --- LIST HARI ---
    val daysDisplay = listOf(
        stringResource(R.string.mon),
        stringResource(R.string.tue),
        stringResource(R.string.wed),
        stringResource(R.string.thu),
        stringResource(R.string.fri)
    )

    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // Background mengikuti Tema yang aktif (bukan Sistem HP)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // --- HEADER ---
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Surface(
                color = MainBlue, // Warna dari Color.kt kamu
                shape = RoundedCornerShape(50),
                modifier = Modifier.width(180.dp).height(45.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.my_schedule),
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // --- TOMBOL HARI ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            daysDisplay.forEachIndexed { index, dayName ->
                val isSelected = selectedIndex == index
                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) MainBlue else Color.Transparent)
                        .clickable { selectedIndex = index },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayName.take(3),
                        color = if (isSelected) White else MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Judul Timeline (Warna Biru biar cantik)
        Text(
            text = stringResource(R.string.timeline_view),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MainBlue
        )

        Spacer(Modifier.height(16.dp))

        // --- TIMELINE LIST ---
        val dailySchedule = schedule.filter { item ->
            val itemDayIndex = getUniversalDayIndex(item.day ?: "")
            itemDayIndex == selectedIndex
        }.sortedBy { it.time ?: "" }

        if (dailySchedule.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.no_schedule_day),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(dailySchedule) { item ->
                    ScheduleItemRow(item)
                }
            }
        }
    }
}

@Composable
fun ScheduleItemRow(item: ScheduleItem) {

    // 1. LOGIKA WARNA GARIS
    val isEven = item.subject.length % 2 == 0
    val accentColor = if (isEven) MainBlue else CardBlue

    // 2. LOGIKA BACKGROUND KARTU (PERBAIKAN DISINI)
    // Kita cek: Apakah Teks di atas Background warnanya PUTIH?
    // Jika YA, berarti kita sedang di Dark Mode.
    // Jika TIDAK (Hitam), berarti kita sedang di Light Mode.
    val isAppInDarkMode = MaterialTheme.colorScheme.onBackground == White

    val containerColor = if (isAppInDarkMode) {
        Color(0xFF252525) // Dark Mode: Abu Gelap
    } else {
        White // Light Mode: Putih Bersih
    }

    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        // JAM
        Text(
            text = (item.time ?: "00:00").take(5),
            modifier = Modifier.width(50.dp).padding(top = 12.dp),
            color = MainBlue, // Jam warna Biru
            fontWeight = FontWeight.Bold
        )

        // KARTU
        Card(
            modifier = Modifier.weight(1f).padding(bottom = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = containerColor // Menggunakan logika baru
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Strip Warna
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(accentColor)
                )

                // Isi Konten
                Column(modifier = Modifier.padding(12.dp).weight(1f)) {
                    Text(
                        text = item.subject,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.time ?: "-",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (item.room != "-" && !item.room.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(accentColor, androidx.compose.foundation.shape.CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.room,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper Hari
fun getUniversalDayIndex(dayString: String): Int {
    val lower = dayString.trim().lowercase()
    return when {
        lower.contains("sen") || lower.contains("mon") || lower.contains("الإثنين") -> 0
        lower.contains("sel") || lower.contains("tue") || lower.contains("الثلاثاء") -> 1
        lower.contains("rab") || lower.contains("wed") || lower.contains("الأربعاء") -> 2
        lower.contains("kam") || lower.contains("thu") || lower.contains("الخميس") -> 3
        lower.contains("jum") || lower.contains("fri") || lower.contains("الجمعة") -> 4
        lower.contains("sab") || lower.contains("sat") || lower.contains("السبت") -> 5
        lower.contains("min") || lower.contains("sun") || lower.contains("الأحد") -> 6
        else -> -1
    }
}  
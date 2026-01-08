package com.joant.kampussmart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joant.kampussmart.ui.theme.*

@Composable
fun HomeScreen(
    schedule: List<ScheduleItem>,
    onBuatJadwalClick: () -> Unit,
    onAICardClick: () -> Unit,
    onProfileClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // --- LOGIKA DETEKSI TEMA (SAMA DENGAN KALENDER) ---
    // Cek apakah teks utama warnanya Putih? (Indikator Dark Mode di Theme.kt kita)
    val isAppInDarkMode = MaterialTheme.colorScheme.onBackground == White

    // Tentukan warna kartu berdasarkan deteksi di atas
    val cardColor = if (isAppInDarkMode) {
        Color(0xFF252525) // Dark Mode: Abu Gelap
    } else {
        White // Light Mode: Putih Bersih
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Kirim 'cardColor' ke komponen di bawahnya
        item { HeaderSection(onProfileClick, cardColor) }

        item { AIChatCard(onClick = onAICardClick) }

        if (schedule.isEmpty()) {
            item { NoScheduleCard(onBuatJadwalClick, cardColor) }
        } else {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.upcoming_task),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = onDeleteClick) {
                        Text(
                            text = stringResource(R.string.delete_all),
                            color = RedDelete,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            items(schedule) { item ->
                UpcomingTaskItem(item, cardColor, isAppInDarkMode)
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun HeaderSection(onProfileClick: () -> Unit, boxColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(R.string.welcome_back),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.student),
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Surface(
            modifier = Modifier.size(50.dp).clickable { onProfileClick() },
            shape = RoundedCornerShape(12.dp),
            // Warna mengikuti logika cerdas tadi
            color = boxColor,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Person,
                    null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun UpcomingTaskItem(item: ScheduleItem, cardColor: Color, isDarkMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        // Warna mengikuti logika cerdas tadi
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    // Pastikan background icon juga menyesuaikan (Biru Gelap vs Biru Muda)
                    .background(
                        if (isDarkMode) DarkBlue else IconBlueBg,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Star,
                    null,
                    tint = MainBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = item.subject,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.day} • ${item.time}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NoScheduleCard(onBuatJadwalClick: () -> Unit, cardColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.upss),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                stringResource(R.string.no_schedule_msg),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onBuatJadwalClick,
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(BlueGradient)
                        .fillMaxHeight()
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.create_schedule), color = White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AIChatCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(110.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(brush = BlueGradient)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(50.dp).background(White.copy(alpha = 0.2f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_kampus),
                        contentDescription = "AI Logo",
                        modifier = Modifier.size(32.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(White)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.ai_card_opening),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    lineHeight = 24.sp
                )
            }
        }
    }
}
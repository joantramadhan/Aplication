package com.joant.kampussmart

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joant.kampussmart.ui.theme.MainBlue

/* =========================
   COLOR CONSTANTS (DARK MODE)
   ========================= */
private val DarkBg = Color(0xFF181818)
private val DarkCard = Color(0xFF202020)
private val DarkTextPrimary = Color.White
private val DarkTextSecondary = Color(0xFFB0B0B0)
private val DarkDivider = Color.White.copy(alpha = 0.12f)

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isDarkMode by remember { mutableStateOf(SettingsStorage.isDarkMode(context)) }
    var isNotifEnabled by remember { mutableStateOf(SettingsStorage.isNotificationEnabled(context)) }
    var selectedLanguage by remember { mutableStateOf(SettingsStorage.getLanguage(context)) }

    var showLanguageDialog by remember { mutableStateOf(false) }

    /* ========= DIALOG BAHASA ========= */
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Pilih Bahasa") },
            text = {
                Column {
                    LanguageItem("Bahasa Indonesia", selectedLanguage == "Indonesia") {
                        SettingsStorage.setLanguage(context, "Indonesia")
                        selectedLanguage = "Indonesia"
                        showLanguageDialog = false
                        (context as? Activity)?.recreate()
                    }

                    LanguageItem("English", selectedLanguage == "English") {
                        SettingsStorage.setLanguage(context, "English")
                        selectedLanguage = "English"
                        showLanguageDialog = false
                        (context as? Activity)?.recreate()
                    }

                    LanguageItem("العربية (Arabic)", selectedLanguage == "Arabic") {
                        SettingsStorage.setLanguage(context, "Arabic")
                        selectedLanguage = "Arabic"
                        showLanguageDialog = false
                        (context as? Activity)?.recreate()
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    /* ========= SCREEN ========= */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkMode) DarkBg else Color(0xFFF5F5F5))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        /* ========= HEADER ========= */
        Text(
            text = "Pengaturan",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) DarkTextPrimary else MainBlue,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        /* ========= TAMPILAN ========= */
        SectionTitle("Tampilan & Bahasa", isDarkMode)

        SettingsCard(isDarkMode) {

            SettingSwitchItem(
                icon = Icons.Default.DarkMode,
                title = "Mode Gelap",
                subtitle = "Tampilan gelap yang nyaman di mata",
                checked = isDarkMode,
                isDarkMode = isDarkMode
            ) { value ->
                SettingsStorage.setDarkMode(context, value)
                isDarkMode = value
                (context as? Activity)?.recreate()
            }

            Divider(color = if (isDarkMode) DarkDivider else Color.LightGray.copy(0.5f))

            SettingClickableItem(
                icon = Icons.Outlined.Language,
                title = "Bahasa",
                subtitle = selectedLanguage,
                isDarkMode = isDarkMode
            ) {
                showLanguageDialog = true
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        /* ========= NOTIFIKASI ========= */
        SectionTitle("Notifikasi", isDarkMode)

        SettingsCard(isDarkMode) {
            SettingSwitchItem(
                icon = Icons.Outlined.Notifications,
                title = "Ingatkan Jadwal Kuliah",
                subtitle = "Notifikasi 15 menit sebelum kelas",
                checked = isNotifEnabled,
                isDarkMode = isDarkMode
            ) { value ->
                SettingsStorage.setNotificationEnabled(context, value)
                isNotifEnabled = value
                Toast.makeText(
                    context,
                    if (value) "Notifikasi Diaktifkan" else "Notifikasi Dimatikan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        /* ========= DATA ========= */
        SectionTitle("Data & Info", isDarkMode)

        SettingsCard(isDarkMode) {

            SettingClickableItem(
                icon = Icons.Outlined.Delete,
                title = "Hapus Semua Jadwal",
                subtitle = "Reset aplikasi ke awal",
                isDestructive = true,
                isDarkMode = isDarkMode
            ) {
                ScheduleStorage.saveSchedule(context, emptyList())
                Toast.makeText(
                    context,
                    "Semua jadwal berhasil dihapus",
                    Toast.LENGTH_LONG
                ).show()
            }

            Divider(color = if (isDarkMode) DarkDivider else Color.LightGray.copy(0.5f))

            SettingClickableItem(
                icon = Icons.Outlined.Info,
                title = "Tentang Aplikasi",
                subtitle = "Versi 1.0.0 • by Joant",
                isDarkMode = isDarkMode
            ) {
                Toast.makeText(
                    context,
                    "Dibuat dengan ❤️ oleh Joant",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "KampusSmart v1.0",
            fontSize = 12.sp,
            color = DarkTextSecondary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

/* =========================
   UI COMPONENTS
   ========================= */

@Composable
fun SectionTitle(text: String, isDarkMode: Boolean) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = if (isDarkMode) DarkTextSecondary else Color.Gray,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun SettingsCard(isDarkMode: Boolean, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkCard else Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            content()
        }
    }
}

@Composable
fun SettingSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    isDarkMode: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            null,
            tint = if (isDarkMode) DarkTextPrimary else MainBlue,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = if (isDarkMode) DarkTextPrimary else Color.Black
            )
            Text(
                subtitle,
                fontSize = 12.sp,
                color = if (isDarkMode) DarkTextSecondary else Color.Gray
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MainBlue
            )
        )
    }
}

@Composable
fun SettingClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isDestructive: Boolean = false,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) Color.Red
            else if (isDarkMode) DarkTextPrimary else MainBlue,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = if (isDestructive) Color.Red
                else if (isDarkMode) DarkTextPrimary else Color.Black
            )
            Text(
                subtitle,
                fontSize = 12.sp,
                color = if (isDarkMode) DarkTextSecondary else Color.Gray
            )
        }

        Icon(
            Icons.Default.ChevronRight,
            null,
            tint = DarkTextSecondary
        )
    }
}

@Composable
fun LanguageItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, modifier = Modifier.weight(1f))
        if (isSelected) {
            Icon(Icons.Default.Check, null, tint = MainBlue)
        }
    }
}

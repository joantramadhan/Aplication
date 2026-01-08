package com.joant.kampussmart

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.joant.kampussmart.ui.theme.MainBlue
import com.joant.kampussmart.ui.theme.KampusSmartTheme

class DashboardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KampusSmartTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var selectedIndex by remember { mutableIntStateOf(0) }
    var scheduleData by remember { mutableStateOf(emptyList<ScheduleItem>()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val data = ScheduleStorage.getSchedule(context)
            withContext(Dispatchers.Main) { scheduleData = data }
        }
    }

    val onDeleteSchedule = {
        ScheduleStorage.saveSchedule(context, emptyList())
        scheduleData = emptyList()
        Toast.makeText(context, "Jadwal dihapus", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        // ❗ IKUT TEMA
        containerColor = MaterialTheme.colorScheme.background,

        bottomBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp)),
                    // ❗ IKUT TEMA
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {

                    BottomNavItem(
                        selected = selectedIndex == 0,
                        onClick = { selectedIndex = 0 },
                        label = stringResource(R.string.nav_home),
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home
                    )

                    BottomNavItem(
                        selected = selectedIndex == 1,
                        onClick = { selectedIndex = 1 },
                        label = stringResource(R.string.nav_calendar),
                        selectedIcon = Icons.Filled.DateRange,
                        unselectedIcon = Icons.Outlined.DateRange
                    )

                    BottomNavItem(
                        selected = selectedIndex == 2,
                        onClick = { selectedIndex = 2 },
                        label = stringResource(R.string.nav_settings),
                        selectedIcon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedIndex) {
                0 -> HomeScreen(
                    schedule = scheduleData,
                    onBuatJadwalClick = {
                        context.startActivity(Intent(context, UploadActivity::class.java))
                    },
                    onAICardClick = {},
                    onProfileClick = {},
                    onDeleteClick = onDeleteSchedule
                )
                1 -> CalendarScreen(schedule = scheduleData)
                2 -> SettingsScreen()
            }
        }
    }
}

@Composable
fun RowScope.BottomNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(if (selected) selectedIcon else unselectedIcon, null)
        },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MainBlue,
            selectedTextColor = MainBlue,
            indicatorColor = MainBlue.copy(alpha = 0.18f),

            // ❗ IKUT TEMA
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

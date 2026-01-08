package com.joant.kampussmart

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.joant.kampussmart.ui.theme.DarkBlueGradient
import com.joant.kampussmart.ui.theme.KampusSmartTheme
import com.joant.kampussmart.ui.theme.MainBlue
import com.joant.kampussmart.ui.theme.White
import kotlinx.coroutines.delay

class MainActivity : BaseActivity() { // Pastikan turunan BaseActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fix bug icon launcher
        if (!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action != null && intent.action == Intent.ACTION_MAIN) {
            finish()
            return
        }

        setContent {
            // Panggil Tema di sini untuk membungkus UI
            KampusSmartTheme {
                SplashScreen(onTimeout = {
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                })
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val context = LocalContext.current
    var titleText by remember { mutableStateOf("Kampus Smart") }
    var subtitleText by remember { mutableStateOf("AI Schedule Planner") }
    var hasPermission by remember { mutableStateOf(false) }

    // Launcher untuk minta izin lokasi secara otomatis
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
            if (isGranted) {
                subtitleText = "Mencari lokasi..."
                LocationUtils.getCurrentLocation(context) { address ->
                    subtitleText = "di $address"
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        // Cek izin dulu
        if (LocationUtils.hasLocationPermission(context)) {
            hasPermission = true
            titleText = "Selamat Datang"
            subtitleText = "Mencari lokasi..."
            LocationUtils.getCurrentLocation(context) { address ->
                subtitleText = "di $address"
            }
        } else {
            // Kalau belum ada izin, minta izin dulu!
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Delay splash screen tetap jalan 3 detik
        delay(3000)
        onTimeout()
    }

    SplashScreenContent(titleText, subtitleText)
}

@Composable
fun SplashScreenContent(title: String, subtitle: String) {
    // Pastikan warna-warna ini ada di ui/theme/Color.kt
    val gradientBrush = remember { Brush.verticalGradient(listOf(DarkBlueGradient, MainBlue)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Pastikan gambar logo_kampus ada di res/drawable
            Image(
                painter = painterResource(id = R.drawable.logo_kampus),
                contentDescription = "Logo",
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 28.sp,
                color = White,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 18.sp,
                color = White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreenContent("Selamat Datang", "di Jakarta 🇮🇩")
}
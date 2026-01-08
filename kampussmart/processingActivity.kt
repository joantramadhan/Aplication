package com.joant.kampussmart

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProcessingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uriStr = intent.getStringExtra("IMAGE_URI")
        val uri = uriStr?.toUri()

        setContent {
            ProcessingScreen(uri) { resultList ->
                if (resultList.isNotEmpty()) {
                    val intent = Intent(this, ResultActivity::class.java).apply {
                        putParcelableArrayListExtra("SCHEDULE_LIST", ArrayList(resultList))
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Tidak ada data valid. Coba foto lebih jelas.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}

@Composable
fun ProcessingScreen(uri: Uri?, onComplete: (List<ScheduleItem>) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var status by remember { mutableStateOf("Memuat Gambar...") }

    LaunchedEffect(Unit) {
        if (uri == null) return@LaunchedEffect

        scope.launch(Dispatchers.Default) {
            try {
                val resizedBitmap = loadResizedBitmap(context, uri)

                withContext(Dispatchers.Main) {
                    bitmap = resizedBitmap
                    status = "Membersihkan Anomali Data..."
                }

                if (resizedBitmap != null) {
                    val image = InputImage.fromBitmap(resizedBitmap, 0)
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            // Panggil Logic V5
                            val cleanedItems = parseV5Filter(visionText.text)
                            onComplete(cleanedItems)
                        }
                        .addOnFailureListener { onComplete(emptyList()) }
                } else {
                    withContext(Dispatchers.Main) { onComplete(emptyList()) }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onComplete(emptyList()) }
            }
        }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF0D47A1)), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (bitmap != null) {
                Image(bitmap!!.asImageBitmap(), null, Modifier.height(200.dp))
            } else {
                CircularProgressIndicator(color = Color.White)
            }
            Spacer(Modifier.height(16.dp))
            Text(status, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// LOGIKA V5: Anti B5, TL.24, & Smart Split
// ==========================================
fun parseV5Filter(rawText: String): List<ScheduleItem> {

    val lines = rawText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

    val subjects = mutableListOf<String>()
    val lecturers = mutableListOf<String>()
    val rooms = mutableListOf<String>()
    val times = mutableListOf<String>()

    // 1. BLACKLIST (Dibuang Total)
    val blacklist = listOf(
        "No.", "Kode", "Matakuliah", "Mata Kuliah",
        "SKS", "Kelas", "Ruang", "Dosen", "Hari", "Waktu",
        "Kampus", "Semester", "Kurikulum", "Cetak", "Hal",
        "TI.24.A.6", "TL.24.A.6", "TL.24", "TI.24", // Kelas
        "B_5", "B5", "B501", "B505", "B506", "FT-FH" // Anomali Ruang (Dibuang jika berdiri sendiri)
    )

    // Regex Patterns
    val codePattern = Regex("^[A-Z]{3}\\d{3}$")  // TIF107
    val classPattern = Regex("T[IL]\\.24")       // TI.24

    // UPDATE: Regex Ruang lebih pintar (B5 tanpa underscore pun kena)
    val roomPattern = Regex("B_?\\d|FT-FH")

    val dayPattern = Regex("(Senin|Selasa|Rabu|Kamis|Jumat|Sabtu|Minggu)")

    for (line in lines) {
        var processedLine = line

        // --- PRE-PROCESS: PISAHKAN RUANG & DOSEN ---
        // Jika ada garis tegak (|), kita pecah dulu
        if (processedLine.contains("|")) {
            val parts = processedLine.split("|")
            // Part 1 biasanya Ruang, Part 2 Dosen
            if (roomPattern.containsMatchIn(parts[0])) {
                rooms.add(parts[0].trim())
                if (parts.size > 1 && parts[1].length > 3) {
                    lecturers.add(parts[1].trim())
                }
                continue // Selesai, skip baris ini
            }
        }

        // --- FILTER BLACKLIST ---
        // Cek apakah baris ini mengandung kata terlarang
        if (blacklist.any { processedLine.contains(it, ignoreCase = true) }) {
            // Pengecualian: Jika ini baris Waktu, jangan dibuang
            if (!dayPattern.containsMatchIn(processedLine)) {

                // Pengecualian 2: Jika ini Ruang "B5..." dan belum masuk list Room,
                // masukkan ke Room dulu sebelum dibuang dari Subject
                if (roomPattern.containsMatchIn(processedLine)) {
                    rooms.add(processedLine)
                }

                continue // Skip (Jangan jadi Subject)
            }
        }

        // Buang Kode Matkul & Kelas
        if (codePattern.matches(processedLine)) continue
        if (classPattern.containsMatchIn(processedLine)) continue

        // Buang Angka SKS
        if (processedLine.all { it.isDigit() } || processedLine.length < 3) continue

        // --- KATEGORISASI ---

        // A. WAKTU
        if (dayPattern.containsMatchIn(processedLine)) {
            times.add(processedLine)
            continue
        }

        // B. RUANG (Backup Check)
        if (roomPattern.containsMatchIn(processedLine)) {
            rooms.add(processedLine)
            continue
        }

        // C. DOSEN
        if ((processedLine.contains(",") || processedLine.contains(".")) && processedLine.length > 10) {
            if (!processedLine.contains(" / ") && !processedLine.matches(Regex(".*\\d{2}\\.\\d{2}.*"))) {
                lecturers.add(processedLine)
                continue
            }
        }

        // D. SISANYA = MATAKULIAH
        // Syarat: Huruf depan besar & bukan simbol aneh
        if (processedLine[0].isUpperCase()) {
            subjects.add(processedLine)
        }
    }

    // --- RAKIT DATA ---
    val finalList = ArrayList<ScheduleItem>()
    val count = subjects.size

    for (i in 0 until count) {
        val rawTime = times.getOrElse(i) { "Senin / 08.00" }
        val cleanDay = if(rawTime.contains("/")) rawTime.split("/")[0].trim() else rawTime
        val cleanTime = if(rawTime.contains("/")) rawTime.split("/")[1].trim() else rawTime

        finalList.add(
            ScheduleItem(
                subject = subjects[i],
                lecturer = lecturers.getOrElse(i) { "-" },
                room = rooms.getOrElse(i) { "-" },
                day = cleanDay,
                time = cleanTime
            )
        )
    }

    return finalList
}

fun loadResizedBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val opt = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opt) }
        var scale = 1
        while (opt.outWidth / scale / 2 >= 1024) scale *= 2
        val opt2 = BitmapFactory.Options().apply { inSampleSize = scale }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opt2) }
    } catch (e: Exception) { null }
}
package com.joant.kampussmart

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.joant.kampussmart.ui.theme.KampusSmartTheme
import com.joant.kampussmart.ui.theme.MainBlue
import java.io.File
import java.io.FileOutputStream

class UploadActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KampusSmartTheme {
                UploadScreen(onBackClick = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current

    /* ================= LAUNCHERS ================= */

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            copyUriToCache(context, it)?.let { cached ->
                context.startActivity(
                    Intent(context, ProcessingActivity::class.java)
                        .putExtra("IMAGE_URI", cached.toString())
                )
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            saveBitmapToCache(context, it)?.let { uri ->
                context.startActivity(
                    Intent(context, ProcessingActivity::class.java)
                        .putExtra("IMAGE_URI", uri.toString())
                )
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.permission_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /* ================= UI ================= */

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.upload_title),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /* ===== PLACEHOLDER ===== */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        MaterialTheme.colorScheme.background,
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.photo_placeholder),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            /* ===== BUTTON CAMERA ===== */
            GradientButton(
                text = stringResource(R.string.take_photo),
                icon = Icons.Default.CameraAlt
            ) {
                if (
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    cameraLauncher.launch(null)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* ===== BUTTON GALLERY ===== */
            GradientButton(
                text = stringResource(R.string.gallery),
                icon = null
            ) {
                galleryLauncher.launch("image/*")
            }
        }
    }
}

/* ================= GRADIENT BUTTON (FIXED) ================= */

@Composable
fun GradientButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    onClick: () -> Unit
) {
    // Gradient lokal → PALING AMAN
    val gradient = Brush.horizontalGradient(
        listOf(
            MainBlue,
            MainBlue.copy(alpha = 0.85f)
        )
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon?.let {
                    Icon(it, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/* ================= HELPERS (TIDAK DIUBAH) ================= */

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? =
    try {
        val file = File(context.cacheDir, "temp_cam.jpg")
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        Uri.fromFile(file)
    } catch (e: Exception) {
        null
    }

fun copyUriToCache(context: Context, sourceUri: Uri): Uri? =
    try {
        val input = context.contentResolver.openInputStream(sourceUri) ?: return null
        val file = File(context.cacheDir, "temp_gal.jpg")
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
        Uri.fromFile(file)
    } catch (e: Exception) {
        null
    }
@Preview(showBackground = true)
@Composable
fun UploadScreenPreview_Light() {
    KampusSmartTheme {
        UploadScreen(onBackClick = {})
    }
}

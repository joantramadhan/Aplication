package com.joant.kampussmart

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import java.util.Locale

object LocationUtils {

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission") // Aman karena kita sudah cek permission di UI
    fun getCurrentLocation(context: Context, onResult: (String) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    // Mengambil data alamat (Maksimal 1 hasil)
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                    if (!addresses.isNullOrEmpty()) {
                        val city = addresses[0].subAdminArea ?: addresses[0].locality ?: "Lokasi"
                        val country = addresses[0].countryName ?: ""
                        // Logika simpel nambah bendera (opsional)
                        val flag = if (country.contains("Indonesia")) "🇮🇩" else "🌍"

                        onResult("$city $flag")
                    } else {
                        onResult("Lokasi tidak dikenal")
                    }
                } catch (e: Exception) {
                    onResult("Gagal memuat alamat")
                }
            } else {
                onResult("GPS tidak aktif")
            }
        }.addOnFailureListener {
            onResult("Gagal mendapatkan lokasi")
        }
    }
}
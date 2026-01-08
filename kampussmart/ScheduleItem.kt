package com.joant.kampussmart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleItem(
    val subject: String,  // Matakuliah
    val lecturer: String, // Dosen
    val room: String,     // Ruangan
    val time: String,     // Hari/Waktu (Gabungan)

    // Field tambahan (opsional, biar tidak error jika kode lama memanggilnya)
    val day: String = "",
    val isSuggested: Boolean = false
) : Parcelable
package com.joant.kampussmart

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Object ini berfungsi sebagai "Gudang" untuk menyimpan data ke memori HP
object ScheduleStorage {
    private const val PREF_NAME = "kampus_smart_prefs"
    private const val KEY_SCHEDULE = "saved_schedule"

    // FUNGSI 1: SIMPAN JADWAL (Dipakai di SavingActivity)
    fun saveSchedule(context: Context, schedule: List<ScheduleItem>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()

        // Ubah List menjadi Teks JSON agar bisa disimpan
        val json = gson.toJson(schedule)

        editor.putString(KEY_SCHEDULE, json)
        editor.apply()
    }

    // FUNGSI 2: AMBIL JADWAL (Dipakai di DashboardActivity)
    fun getSchedule(context: Context): List<ScheduleItem> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SCHEDULE, null)

        return if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<ScheduleItem>>() {}.type
            // Ubah Teks JSON kembali menjadi List
            gson.fromJson(json, type)
        } else {
            emptyList() // Kembalikan list kosong jika belum ada data
        }
    }
}
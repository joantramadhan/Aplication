package com.joant.kampussmart

import android.content.Context

object SettingsStorage {
    private const val PREF_NAME = "kampus_smart_settings"

    // Kunci Penyimpanan
    private const val KEY_DARK_MODE = "pref_dark_mode"
    private const val KEY_NOTIF = "pref_notification"
    private const val KEY_LANGUAGE = "pref_language"

    // --- DARK MODE ---
    fun setDarkMode(context: Context, isDark: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_MODE, isDark).apply()
    }

    fun isDarkMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        // Default false (Mode Terang)
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    // --- NOTIFIKASI ---
    fun setNotificationEnabled(context: Context, isEnabled: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_NOTIF, isEnabled).apply()
    }

    fun isNotificationEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_NOTIF, true) // Default Aktif
    }

    // --- BAHASA ---
    fun setLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "Indonesia") ?: "Indonesia"
    }
}
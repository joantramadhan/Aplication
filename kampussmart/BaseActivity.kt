package com.joant.kampussmart

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity // Ini tetap ComponentActivity di import, tapi kelas kita BaseActivity
import java.util.Locale

open class BaseActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val language = SettingsStorage.getLanguage(newBase)
        val localeCode = when (language) {
            "English" -> "en"
            "العربية (Arabic)" -> "ar" // Sesuaikan string persis dari SettingsScreen
            "Arabic" -> "ar"
            else -> "in"
        }

        val context = updateLocale(newBase, localeCode)
        super.attachBaseContext(context)
    }

    private fun updateLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale) // PENTING: Agar layout Arab jadi Kanan-ke-Kiri

        return context.createConfigurationContext(config)
    }
}
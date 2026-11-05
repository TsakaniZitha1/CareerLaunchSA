package com.example.careerlaunchsa.data
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        // Save selected language to SharedPreferences
        val prefs = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("SELECTED_LANGUAGE", languageCode).apply()

        return context.createConfigurationContext(config)
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
        return prefs.getString("SELECTED_LANGUAGE", "en") ?: "en"
    }
}

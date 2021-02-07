package com.axel_stein.ap_diary.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.preference.PreferenceManager

class AppSettings(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        applyTheme(prefs.getString("theme", "system") ?: "system")
    }

    fun applyTheme(theme: String) {
        setDefaultNightMode(
            when (theme) {
                "light" -> MODE_NIGHT_NO
                "dark" -> MODE_NIGHT_YES
                else -> MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
}
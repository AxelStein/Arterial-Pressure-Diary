package com.axel_stein.ap_diary.data

import android.content.Context
import androidx.preference.PreferenceManager

class AppSettings(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun showSplashScreen() = prefs.getBoolean("show_splash_screen", true)

    fun setShowSplashScreen(show: Boolean) {
        prefs.edit().putBoolean("show_splash_screen", show).apply()
    }
}
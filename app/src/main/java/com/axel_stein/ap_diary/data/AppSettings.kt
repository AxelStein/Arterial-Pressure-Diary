package com.axel_stein.ap_diary.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.preference.PreferenceManager
import org.joda.time.LocalTime

private const val REMINDER_MORNING_TIME = "reminder_morning_time"
private const val REMINDER_EVENING_TIME = "reminder_evening_time"

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

    fun setReminderMorningTime(hours: Int, minutes: Int) {
        setReminderTime(REMINDER_MORNING_TIME, hours, minutes)
    }

    fun setReminderEveningTime(hours: Int, minutes: Int) {
        setReminderTime(REMINDER_EVENING_TIME, hours, minutes)
    }

    private fun setReminderTime(key: String, hours: Int, minutes: Int) {
        with(LocalTime(hours, minutes)) {
            prefs.edit().putString(key, this.toString()).apply()
        }
    }

    fun getReminderMorningTime(): LocalTime = getReminderTime(REMINDER_MORNING_TIME)

    fun getReminderEveningTime(): LocalTime = getReminderTime(REMINDER_EVENING_TIME)

    private fun getReminderTime(key: String): LocalTime {
        return with(prefs.getString(key, null)) {
            if (this == null) {
                val hour = if (key == REMINDER_MORNING_TIME) { 8 } else { 20 }
                setReminderMorningTime(hour, 0)
                LocalTime(hour, 0)
            } else {
                LocalTime(this)
            }
        }
    }
}
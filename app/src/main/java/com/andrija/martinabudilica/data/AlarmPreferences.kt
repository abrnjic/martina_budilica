package com.andrija.martinabudilica.data

import android.content.Context

data class AlarmSettings(
    val hour: Int = 7,
    val minute: Int = 0,
    val reminder: String = "Ne zaboravi da te danas čeka nešto lijepo!",
    val enabled: Boolean = false
)

class AlarmPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("martina_alarm", Context.MODE_PRIVATE)

    fun load() = AlarmSettings(
        hour = prefs.getInt("hour", 7),
        minute = prefs.getInt("minute", 0),
        reminder = prefs.getString("reminder", null)
            ?: "Ne zaboravi da te danas čeka nešto lijepo!",
        enabled = prefs.getBoolean("enabled", false)
    )

    fun save(settings: AlarmSettings) {
        prefs.edit()
            .putInt("hour", settings.hour)
            .putInt("minute", settings.minute)
            .putString("reminder", settings.reminder)
            .putBoolean("enabled", settings.enabled)
            .apply()
    }
}

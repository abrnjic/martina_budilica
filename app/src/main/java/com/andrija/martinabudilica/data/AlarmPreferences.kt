package com.andrija.martinabudilica.data

import android.content.Context

data class AlarmSettings(
    val hour: Int = 7,
    val minute: Int = 0,
    val reminder: String = "Ne zaboravi da te danas čeka nešto lijepo!",
    val enabled: Boolean = false,
    val ringtoneUri: String? = null,
    val daysOfWeek: Set<Int> = setOf(2, 3, 4, 5, 6), // Mon-Fri by default (Calendar.MONDAY = 2)
    val bedtimeReminderEnabled: Boolean = false
)

class AlarmPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("martina_alarm", Context.MODE_PRIVATE)

    fun load(): AlarmSettings {
        val daysString = prefs.getString("daysOfWeek", "2,3,4,5,6") ?: "2,3,4,5,6"
        val daysSet = if (daysString.isEmpty()) emptySet() else daysString.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        return AlarmSettings(
            hour = prefs.getInt("hour", 7),
            minute = prefs.getInt("minute", 0),
            reminder = prefs.getString("reminder", null)
                ?: "Ne zaboravi da te danas čeka nešto lijepo!",
            enabled = prefs.getBoolean("enabled", false),
            ringtoneUri = prefs.getString("ringtoneUri", null),
            daysOfWeek = daysSet,
            bedtimeReminderEnabled = prefs.getBoolean("bedtimeReminderEnabled", false)
        )
    }

    fun save(settings: AlarmSettings) {
        val daysString = settings.daysOfWeek.joinToString(",")
        prefs.edit()
            .putInt("hour", settings.hour)
            .putInt("minute", settings.minute)
            .putString("reminder", settings.reminder)
            .putBoolean("enabled", settings.enabled)
            .putString("ringtoneUri", settings.ringtoneUri)
            .putString("daysOfWeek", daysString)
            .putBoolean("bedtimeReminderEnabled", settings.bedtimeReminderEnabled)
            .apply()
    }
}

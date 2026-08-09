package com.andrija.martinabudilica.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.andrija.martinabudilica.R
import com.andrija.martinabudilica.data.AlarmPreferences

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val settings = AlarmPreferences(context).load()
        if (!settings.enabled && intent?.action != "ACTION_WAKEUP_CHECK") return

        val notificationManager = context.getSystemService(NotificationManager::class.java)

        when (intent?.action) {
            "ACTION_BEDTIME_REMINDER" -> {
                val channel = NotificationChannel(
                    "bedtime_channel",
                    "Podsjetnik za spavanje",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
                val notification = NotificationCompat.Builder(context, "bedtime_channel")
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle("Vrijeme je za pripremu! 🌙")
                    .setContentText("Imaš još 8 sati do buđenja. Laku noć, Martina.")
                    .setAutoCancel(true)
                    .build()
                notificationManager.notify(1405, notification)
            }
            "ACTION_WAKEUP_CHECK" -> {
                // Schedule the "real" alarm in 2 minutes if she ignores this
                val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                    action = "ACTION_WAKEUP_ALARM_FALLBACK"
                }
                val pi = PendingIntent.getBroadcast(context, 1406, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                val manager = context.getSystemService(android.app.AlarmManager::class.java)
                manager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2 * 60 * 1000L, pi)

                val dismissIntent = Intent(context, AlarmReceiver::class.java).apply { action = "ACTION_WAKEUP_DISMISS" }
                val dismissPi = PendingIntent.getBroadcast(context, 1407, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                val channel = NotificationChannel("wakeup_check", "Provjera budnosti", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
                val notification = NotificationCompat.Builder(context, "wakeup_check")
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle("Jesi li stvarno budna? 👀")
                    .setContentText("Klikni ovdje da otkažeš alarm koji zvoni za 2 minute!")
                    .setContentIntent(dismissPi)
                    .setAutoCancel(true)
                    .build()
                notificationManager.notify(1408, notification)
            }
            "ACTION_WAKEUP_DISMISS" -> {
                val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                    action = "ACTION_WAKEUP_ALARM_FALLBACK"
                }
                val pi = PendingIntent.getBroadcast(context, 1406, alarmIntent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
                if (pi != null) {
                    val manager = context.getSystemService(android.app.AlarmManager::class.java)
                    manager.cancel(pi)
                }
                notificationManager.cancel(1408)
            }
            "ACTION_WAKEUP_ALARM_FALLBACK", null -> {
                // Default Alarm Trigger
                val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val fullScreenIntent = PendingIntent.getActivity(
                    context,
                    1403,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
        
                val ringtoneUriStr = settings.ringtoneUri
                val sound = if (ringtoneUriStr != null) android.net.Uri.parse(ringtoneUriStr) else RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Martinin alarm",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Šaljivi alarm za dobro jutro"
                    setSound(sound, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build())
                    lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                }
                notificationManager.createNotificationChannel(channel)
        
                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle("Dobro jutro, Martina! ☀️")
                    .setContentText("Vrijeme je da uljepšaš dan svojim buđenjem.")
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setSound(sound)
                    .setFullScreenIntent(fullScreenIntent, true)
                    .build()
        
                notificationManager.notify(NOTIFICATION_ID, notification)
                context.startActivity(alarmIntent)
                if (intent?.action != "ACTION_WAKEUP_ALARM_FALLBACK") {
                    AlarmScheduler.schedule(context, settings)
                }
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "martina_alarm_channel"
        const val NOTIFICATION_ID = 1404
    }
}

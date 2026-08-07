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
        if (!settings.enabled) return

        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val fullScreenIntent = PendingIntent.getActivity(
            context,
            1403,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
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
        AlarmScheduler.schedule(context, settings)
    }

    companion object {
        const val CHANNEL_ID = "martina_alarm_channel"
        const val NOTIFICATION_ID = 1404
    }
}

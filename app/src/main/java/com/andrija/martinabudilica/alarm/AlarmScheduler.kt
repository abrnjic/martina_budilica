package com.andrija.martinabudilica.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.andrija.martinabudilica.data.AlarmSettings
import java.util.Calendar

object AlarmScheduler {
    private const val REQUEST_CODE = 1402
    private const val REQUEST_CODE_BEDTIME = 1403
    private const val REQUEST_CODE_WAKEUP = 1404

    fun schedule(context: Context, settings: AlarmSettings): Boolean {
        val manager = context.getSystemService(AlarmManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !manager.canScheduleExactAlarms()) {
            return false
        }

        val intent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, settings.hour)
            set(Calendar.MINUTE, settings.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            
            if (settings.daysOfWeek.isNotEmpty()) {
                while (!settings.daysOfWeek.contains(get(Calendar.DAY_OF_WEEK))) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }

        manager.setAlarmClock(AlarmManager.AlarmClockInfo(next.timeInMillis, intent), intent)
        
        if (settings.bedtimeReminderEnabled) {
            scheduleBedtimeReminder(context, next.timeInMillis)
        } else {
            cancelBedtimeReminder(context)
        }
        
        return true
    }

    fun cancel(context: Context) {
        val manager = context.getSystemService(AlarmManager::class.java)
        val intent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (intent != null) manager.cancel(intent)
        cancelBedtimeReminder(context)
        cancelWakeUpCheck(context)
    }

    fun scheduleTest(context: Context, delayMillis: Long = 10_000): Boolean {
        val manager = context.getSystemService(AlarmManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !manager.canScheduleExactAlarms()) {
            return false
        }
        val intent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        manager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + delayMillis,
            intent
        )
        return true
    }

    fun scheduleBedtimeReminder(context: Context, alarmTimeMillis: Long) {
        val manager = context.getSystemService(AlarmManager::class.java)
        val intent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_BEDTIME,
            Intent(context, AlarmReceiver::class.java).apply {
                action = "ACTION_BEDTIME_REMINDER"
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val bedtime = alarmTimeMillis - 8 * 60 * 60 * 1000L
        if (bedtime > System.currentTimeMillis()) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, bedtime, intent)
        }
    }

    fun cancelBedtimeReminder(context: Context) {
        val manager = context.getSystemService(AlarmManager::class.java)
        val intent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_BEDTIME,
            Intent(context, AlarmReceiver::class.java).apply {
                action = "ACTION_BEDTIME_REMINDER"
            },
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (intent != null) manager.cancel(intent)
    }

    fun scheduleWakeUpCheck(context: Context, delayMinutes: Int = 5) {
        val manager = context.getSystemService(AlarmManager::class.java)
        val intent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_WAKEUP,
            Intent(context, AlarmReceiver::class.java).apply {
                action = "ACTION_WAKEUP_CHECK"
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        manager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + delayMinutes * 60 * 1000L,
            intent
        )
    }
    
    fun cancelWakeUpCheck(context: Context) {
        val manager = context.getSystemService(AlarmManager::class.java)
        val intent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_WAKEUP,
            Intent(context, AlarmReceiver::class.java).apply {
                action = "ACTION_WAKEUP_CHECK"
            },
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (intent != null) manager.cancel(intent)
    }
}

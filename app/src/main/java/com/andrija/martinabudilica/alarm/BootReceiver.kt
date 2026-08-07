package com.andrija.martinabudilica.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.andrija.martinabudilica.data.AlarmPreferences

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val settings = AlarmPreferences(context).load()
        if (settings.enabled) AlarmScheduler.schedule(context, settings)
    }
}

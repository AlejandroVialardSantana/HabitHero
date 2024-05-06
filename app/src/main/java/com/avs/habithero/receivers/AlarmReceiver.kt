package com.avs.habithero.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.avs.habithero.helpers.NotificationHelper

/**
 * Broadcast receiver para manejar las alarmas de los hábitos
 */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitTitle = intent.getStringExtra("habit_title") ?: "Reminder"
        NotificationHelper.createNotification(context, habitTitle)
    }
}
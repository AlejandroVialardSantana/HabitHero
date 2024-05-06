package com.avs.habithero.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.avs.habithero.helpers.NotificationHelper

/**
 * Broadcast receiver para manejar el evento de encendido del dispositivo
 */
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NotificationHelper.reprogramAlarms(context)
        }
    }
}

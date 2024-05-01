package com.avs.habithero.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.avs.habithero.helpers.NotificationHelper

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NotificationHelper.reprogramAlarms(context)
        }
    }
}

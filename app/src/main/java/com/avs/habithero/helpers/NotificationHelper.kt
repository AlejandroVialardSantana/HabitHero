package com.avs.habithero.helpers

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.avs.habithero.R
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.util.Log
import com.avs.habithero.receivers.AlarmReceiver
import java.util.Calendar
import kotlin.math.abs

object NotificationHelper {
    fun createNotification(context: Context, habitTitle: String) {
        val channelId = "habit_reminder_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val channel = NotificationChannel(channelId, "Habit Reminders", NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(soundUri, AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_NOTIFICATION).build())
                enableVibration(true)
                vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Habit Reminder")
            .setContentText("Time for your habit: $habitTitle")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(habitTitle.hashCode(), notification)
    }

    fun reprogramAlarms(context: Context) {
        val sharedPreferences = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)

        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            if (key.startsWith("habit_") && key.contains("_time_")) {
                val parts = key.split("_time_")
                val habitId = parts[0].substring(6)

                val dayIndex = parts[1].toInt()
                scheduleAlarmFromPreferences(context, habitId, dayIndex, value.toString())
            }
        }

        Log.d("NotificationHelper", "Alarms reprogrammed")
    }

    private fun scheduleAlarmFromPreferences(context: Context, habitId: String, dayIndex: Int, time: String) {
        val sharedPreferences = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
        val title = sharedPreferences.getString("habit_" + habitId + "_title_" + dayIndex, "Reminder");

        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.DAY_OF_WEEK, dayIndex + 2)
        }

        if (calendar.before(now)) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("habit_title", title)
        }
        val requestCode = abs((habitId.hashCode() * 100 + (dayIndex - 2)))
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        Log.d("NotificationHelper", "Scheduling alarm for " + habitId + " at " + hour + ":" + minute + " on day index " + dayIndex + " with request code " + requestCode)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
}
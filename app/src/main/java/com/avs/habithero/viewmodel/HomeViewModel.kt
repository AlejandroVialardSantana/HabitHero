package com.avs.habithero.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.CalendarContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.avs.habithero.models.Habit
import com.avs.habithero.receivers.AlarmReceiver
import com.avs.habithero.repositories.AuthRepository
import com.avs.habithero.repositories.HabitRepository
import java.util.Calendar
import java.util.TimeZone

class HomeViewModel(private val habitRepository: HabitRepository) : ViewModel() {

    private val userId = AuthRepository().getCurrentUserId()
    val habits: LiveData<List<Habit>> = habitRepository.habits

    init {
        habitRepository.getHabits(userId)
    }

    fun addHabit(habit: Habit, context: Context) {
        habitRepository.addHabit(habit, userId)
        scheduleAlarmForHabit(habit, context)
        addEventToCalendar(context, habit)
    }

    fun deleteHabit(habitId: String) {
        habitRepository.deleteHabit(habitId, userId)
    }

    fun updateHabit(habit: Habit, context: Context) {
        habitRepository.updateHabit(habit, userId)
        scheduleAlarmForHabit(habit, context)
        addEventToCalendar(context, habit)
    }

    fun getHabitById(habitId: String): LiveData<Habit?> {
        return habitRepository.getHabitById(habitId, userId)
    }

    private fun scheduleAlarmForHabit(habit: Habit, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = Calendar.getInstance()

        habit.notificationTimes.forEach { time ->
            val parts = time.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            habit.selectedDays.forEachIndexed { index, isSelected ->
                if (isSelected) {
                    val calendar = Calendar.getInstance()  // Usa la zona horaria local
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)

                    Log.d("HomeViewModel", "Current time: ${now.time}")
                    Log.d("HomeViewModel", "Selected days: ${habit.selectedDays} and index: $index")

                    val dayOfWeek = index + 2
                    Log.d("HomeViewModel", "Day of week: $dayOfWeek")
                    calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)

                    if (calendar.before(now)) {
                        calendar.add(Calendar.WEEK_OF_YEAR, 1)
                    }

                    val requestCode = habit.hashCode() * 100 + index  // Ejemplo de código de solicitud único
                    val intent = Intent(context, AlarmReceiver::class.java).apply {
                        putExtra("habit_title", habit.title)
                    }
                    val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                    Log.d("HomeViewModel", "Alarm scheduled for ${habit.title} at ${calendar.time} with requestCode $requestCode")
                }
            }
        }
    }

    private fun addEventToCalendar(context: Context, habit: Habit) {
        val startTime = Calendar.getInstance()

        val endTime = Calendar.getInstance().apply {
            add(Calendar.HOUR, habit.duration)
        }

        Log.d("HomeViewModel", "Adding event to calendar: ${habit.title} from ${startTime.time} to ${endTime.time}")

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, habit.title)
            putExtra(CalendarContract.Events.EVENT_LOCATION, "Home")
            putExtra(CalendarContract.Events.DESCRIPTION, "Scheduled via HabitHero app.")
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // Importante cuando se llama fuera de un contexto de actividad
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Log or handle case where no app can handle the intent
            Log.e("HomeViewModel", "No app can handle the intent")
        }
    }
}
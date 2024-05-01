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
import kotlin.math.abs

class HomeViewModel(private val habitRepository: HabitRepository) : ViewModel() {

    private val userId = AuthRepository().getCurrentUserId()
    val habits: LiveData<List<Habit>> = habitRepository.habits

    init {
        habitRepository.getHabits(userId)
    }

    fun addHabit(habit: Habit, context: Context) {
        habitRepository.addHabit(habit, userId) { habitId ->
            habit.habitId = habitId
            scheduleAlarmForHabit(habit, context)
            // addEventToCalendar(context, habit)
            saveAlarmDetails(context, habit)
        }
    }

    fun deleteHabit(habitId: String) {
        habitRepository.deleteHabit(habitId, userId)
    }

    fun updateHabit(habit: Habit, context: Context) {
        habitRepository.updateHabit(habit, userId)
        scheduleAlarmForHabit(habit, context)
        // addEventToCalendar(context, habit)
        saveAlarmDetails(context, habit)
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
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)

                    val dayOfWeek = index + 2
                    calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)

                    if (calendar.before(now)) {
                        calendar.add(Calendar.WEEK_OF_YEAR, 1)
                    }


                    val requestCode = abs((habit.habitId.hashCode() * 100 + index))
                    val intent = Intent(context, AlarmReceiver::class.java).apply {
                        putExtra("habit_title", habit.title)
                    }
                    val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                    Log.d("HomeViewModel", "Scheduling alarm for ${habit.habitId} at day $dayOfWeek with time $time with requestCode $requestCode")
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
            }
        }
    }

    private fun saveAlarmDetails(context: Context, habit: Habit) {
        val sharedPreferences = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        sharedPreferences.all.keys.filter { it.startsWith("habit_${habit.habitId}_time_") }.forEach {
            editor.remove(it)
        }

        habit.selectedDays.forEachIndexed { index, isSelected ->
            if (isSelected) {
                habit.notificationTimes.forEach { time ->
                    val dayOfWeek = index + 2
                    val key = "habit_${habit.habitId}_time_${dayOfWeek}"
                    val keyTitle = "habit_${habit.habitId}_title_$dayOfWeek"
                    editor.putString(key, time)
                    Log.d("HomeViewModel", "Saving alarm for ${habit.habitId} at day $dayOfWeek with time $time")
                }
            }
        }
        editor.apply()
    }
}
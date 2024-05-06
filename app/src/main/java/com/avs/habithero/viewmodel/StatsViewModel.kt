package com.avs.habithero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.avs.habithero.models.Habit
import com.avs.habithero.repositories.AuthRepository
import com.avs.habithero.repositories.HabitRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatsViewModel(private val habitRepository: HabitRepository): ViewModel() {

    private val userId = AuthRepository().getCurrentUserId() ?: ""
    val habits: LiveData<List<Habit>> = habitRepository.habits

    init {
        habitRepository.getHabits(userId)
    }

    // Método que calcula la cantidad de hábitos completados y no completados por día de la semana
    fun calculateHabitsCompletion(habits: List<Habit>): Pair<List<Float>, List<Float>> {
        val completed = FloatArray(7)
        val notCompleted = FloatArray(7)
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = adjustDayOfWeekIndex(calendar.get(Calendar.DAY_OF_WEEK))
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (habit in habits) {
            habit.selectedDays.forEachIndexed { index, isSelected ->
                if (isSelected && index <= currentDayOfWeek) {
                    calendar.set(Calendar.DAY_OF_WEEK, index + 2)
                    val dateKey = formatter.format(calendar.time)
                    val completionStatus = habit.completions[dateKey]
                    if (completionStatus == true) {
                        completed[index]++
                    } else {
                        notCompleted[index]++
                    }
                }
            }
        }
        return completed.toList() to notCompleted.toList()
    }

    // Método que ajusta el índice del día de la semana para que empiece en 0 (lunes) y termine en 6 (domingo)
    private fun adjustDayOfWeekIndex(dayOfWeek: Int): Int {
        return if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
    }
}
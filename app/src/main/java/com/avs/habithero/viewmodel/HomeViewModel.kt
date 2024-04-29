package com.avs.habithero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.avs.habithero.model.Habit
import com.avs.habithero.repository.AuthRepository
import com.avs.habithero.repository.HabitRepository

class HomeViewModel(private val habitRepository: HabitRepository) : ViewModel() {

    private val userId = AuthRepository().getCurrentUserId()
    val habits: LiveData<List<Habit>> = habitRepository.getHabits(userId)

    fun addHabit(habit: Habit) {
        habitRepository.addHabit(habit, userId)
    }
}
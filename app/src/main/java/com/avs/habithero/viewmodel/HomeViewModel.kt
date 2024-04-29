package com.avs.habithero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.avs.habithero.model.Habit
import com.avs.habithero.repository.HabitRepository

class HomeViewModel(private val habitRepository: HabitRepository) : ViewModel() {

    val habits: LiveData<List<Habit>> = habitRepository.getHabits()

    fun addHabit(habit: Habit) {
        habitRepository.addHabit(habit)
    }
}
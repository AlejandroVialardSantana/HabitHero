package com.avs.habithero.model

data class Habit(
    var habitId: String? = null,
    val title: String = "",
    val type: String = "",
    val frequency: Int = 0,
    val duration: Int = 0,
    val isCompleted: Boolean = false
)

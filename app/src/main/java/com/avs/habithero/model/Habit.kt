package com.avs.habithero.model

data class Habit(
    var habitId: String? = null,
    val title: String = "",
    val type: String = "",
    val frequencyType: String = "Weekly",
    val selectedDays: List<Boolean> = listOf(false, false, false, false, false, false, false),
    val notificationTimes: List<String> = listOf(),
    val duration: Int = 0,
    val isCompleted: Boolean = false
) {
    val frequency: Int
        get() = selectedDays.count { it }
}
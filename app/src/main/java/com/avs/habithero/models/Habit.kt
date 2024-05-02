package com.avs.habithero.models

data class Habit(
    var habitId: String? = null,
    val title: String = "",
    val type: String = "",
    val frequencyType: String = "Weekly",
    val selectedDays: List<Boolean> = listOf(false, false, false, false, false, false, false),
    val notificationTimes: List<String> = listOf(),
    val duration: Int = 0,
    var completions: MutableMap<String, Boolean> = mutableMapOf(),
)
 {
    val frequency: Int
        get() = selectedDays.count { it }
}
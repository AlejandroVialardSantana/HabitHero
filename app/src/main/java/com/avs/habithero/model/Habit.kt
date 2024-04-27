package com.avs.habithero.model

data class Habit(
    val habitId: String,
    val title: String,
    val type: String,
    val frequency: Int,
    val duration: Int,
)
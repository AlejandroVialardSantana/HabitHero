package com.avs.habithero.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avs.habithero.model.Habit
import com.google.firebase.firestore.FirebaseFirestore

class HabitRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getHabits(): LiveData<List<Habit>> {
        val mutableData = MutableLiveData<List<Habit>>()
        db.collection("habits").get().addOnSuccessListener { snapshot ->
            val habits = snapshot.toObjects(Habit::class.java)
            mutableData.value = habits
        }
        return mutableData
    }
}

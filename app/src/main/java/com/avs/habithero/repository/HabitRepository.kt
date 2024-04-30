package com.avs.habithero.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avs.habithero.model.Habit
import com.google.firebase.firestore.FirebaseFirestore

class HabitRepository {
    private val db = FirebaseFirestore.getInstance()
    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>> get() = _habits

    fun getHabits(userId: String) {
        db.collection("users").document(userId).collection("habits")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("HabitRepository", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val habits = snapshot.toObjects(Habit::class.java)
                    _habits.postValue(habits)
                } else {
                    Log.d("HabitRepository", "Current data: null")
                }
            }
    }

    fun addHabit(habit: Habit, userId: String) {
        db.collection("users").document(userId).collection("habits")
            .add(habit)
            .addOnSuccessListener { documentReference ->
                db.collection("users").document(userId).collection("habits")
                    .document(documentReference.id)
                    .update("habitId", documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.w("HabitRepository", "Error adding document", e)
            }
    }

    fun deleteHabit(habitId: String, userId: String) {
        if (habitId.isNotBlank()) {
            db.collection("users").document(userId).collection("habits").document(habitId)
                .delete()
                .addOnSuccessListener {
                    val updatedHabits = _habits.value?.toMutableList() ?: mutableListOf()
                    updatedHabits.removeIf { it.habitId == habitId }
                    _habits.value = updatedHabits
                    Log.d("HabitRepository", "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w("HabitRepository", "Error deleting document", e)
                }
        } else {
            Log.e("HabitRepository", "Error: Habit ID is blank or null")
        }
    }

    fun updateHabit(habit: Habit, userId: String) {
        if (habit.habitId != null) {
            db.collection("users").document(userId).collection("habits").document(habit.habitId!!)
                .set(habit)
                .addOnSuccessListener {
                    Log.d("HabitRepository", "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { e ->
                    Log.w("HabitRepository", "Error updating document", e)
                }
        } else {
            Log.e("HabitRepository", "Error: Habit ID is null")
        }
    }

    fun getHabitById(habitId: String, userId: String): LiveData<Habit?> {
        val habitLiveData = MutableLiveData<Habit?>()
        db.collection("users").document(userId).collection("habits").document(habitId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val habit = documentSnapshot.toObject(Habit::class.java)
                habitLiveData.postValue(habit)
            }
            .addOnFailureListener { exception ->
                Log.e("HabitRepository", "Error loading habit", exception)
                habitLiveData.postValue(null)
            }
        return habitLiveData
    }
}

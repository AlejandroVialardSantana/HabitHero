package com.avs.habithero.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avs.habithero.model.Habit
import com.google.firebase.firestore.FirebaseFirestore

class HabitRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getHabits(userId: String): LiveData<List<Habit>> {
        val mutableData = MutableLiveData<List<Habit>>()
        db.collection("users").document(userId).collection("habits")
            .get()
            .addOnSuccessListener { snapshot ->
                val habits = snapshot.toObjects(Habit::class.java)
                mutableData.value = habits
            }
            .addOnFailureListener { e ->
                Log.w("HabitRepository", "Error getting documents.", e)
            }
        return mutableData
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

}

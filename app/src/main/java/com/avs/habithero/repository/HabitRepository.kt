package com.avs.habithero.repository

import android.util.Log
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
            Log.d("HabitRepository", "Habits loaded: ${habits.size}")
            mutableData.value = habits
        }.addOnFailureListener { exception ->
            Log.w("HabitRepository", "Error loading habits", exception)
        }
        return mutableData
    }

    fun addHabit(habit: Habit) {
        db.collection("habits").add(habit)
            .addOnSuccessListener { documentReference ->
                habit.habitId = documentReference.id
                // TODO: Cambiar esta forma de actualizar el id
                db.collection("habits").document(documentReference.id)
                    .update("habitId", documentReference.id)
                    .addOnSuccessListener {
                        Log.d("HabitRepository", "DocumentSnapshot successfully updated with id")
                    }
                    .addOnFailureListener { e ->
                        Log.w("HabitRepository", "Error updating document", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("HabitRepository", "Error adding document", e)
            }
    }

}

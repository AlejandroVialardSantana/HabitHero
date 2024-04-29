package com.avs.habithero.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avs.habithero.R
import com.avs.habithero.model.Habit

class HabitAdapter(private var habits: MutableList<Habit>,
                    private val onEditClicked: (Habit) -> Unit,
                    private val onDeleteClicked: (Habit, Int) -> Unit) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {
    class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.habitName)
        val habitTime: TextView = view.findViewById(R.id.habitTime)
        val checkBoxCompleted: CheckBox = view.findViewById(R.id.checkBoxCompleted)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.name.text = habit.title
        holder.habitTime.text = habit.duration.toString()
        holder.checkBoxCompleted.isChecked = habit.isCompleted

        holder.itemView.findViewById<View>(R.id.editHabit).setOnClickListener {
            onEditClicked(habit)
        }

        holder.itemView.findViewById<View>(R.id.deleteHabit).setOnClickListener {
            onDeleteClicked(habit, position)
        }
    }

    override fun getItemCount(): Int {
        return habits.size
    }

    fun updateData(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        habits.removeAt(position)
        notifyItemRemoved(position)
    }
}
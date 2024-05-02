package com.avs.habithero.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.avs.habithero.adapters.HabitAdapter
import com.avs.habithero.databinding.FragmentHomeBinding
import com.avs.habithero.repositories.HabitRepository
import com.avs.habithero.viewmodel.HomeViewModel
import java.util.Calendar

class HomeFragment: Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = HomeViewModel(HabitRepository())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeHabits()
        scheduleMidnightRefresh()

        binding.addHabit.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddHabitFragment(null)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeHabits() {
        viewModel.habits.observe(viewLifecycleOwner) { habits ->
            habitAdapter.updateData(habits)
            updateWelcomeMessageVisibility(habits.isEmpty())
        }
    }

    private fun updateWelcomeMessageVisibility(isEmpty: Boolean) {
        binding.welcomeMessage.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.subWelcomeMessage.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            mutableListOf(),
            onEditClicked = {
                val action = HomeFragmentDirections.actionHomeFragmentToAddHabitFragment(it.habitId)
                findNavController().navigate(action)
            },
            onDeleteClicked = { habit, position ->
                viewModel.deleteHabit(habit.habitId?:"")
                habitAdapter.removeItem(position)
            },
            onCompletedClicked = { habit, isChecked ->
                val currentDate = habitAdapter.getTodayDateString()
                habit.completions[currentDate] = isChecked
                viewModel.updateHabitCompletion(habit)
            }

        )
        binding.habitsRecyclerView.adapter = habitAdapter
        binding.habitsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun scheduleMidnightRefresh() {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val millisUntilMidnight = calendar.timeInMillis - now

        Handler(Looper.getMainLooper()).postDelayed({
            refreshHabitsForNewDay()
        }, millisUntilMidnight)
    }

    private fun refreshHabitsForNewDay() {
       habitAdapter.notifyDataSetChanged()
    }
}
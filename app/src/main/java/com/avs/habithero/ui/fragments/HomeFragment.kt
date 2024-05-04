package com.avs.habithero.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.avs.habithero.adapters.HabitAdapter
import com.avs.habithero.databinding.FragmentHomeBinding
import com.avs.habithero.repositories.AuthRepository
import com.avs.habithero.repositories.HabitRepository
import com.avs.habithero.viewmodel.AuthViewModel
import com.avs.habithero.viewmodel.HomeViewModel
import java.util.Calendar

class HomeFragment: Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var viewModel: HomeViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = HomeViewModel(HabitRepository())
        authViewModel = AuthViewModel(AuthRepository())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeHabits()
        scheduleMidnightRefresh()

        binding.loading.visibility = View.VISIBLE
        binding.welcomeMessage.visibility = View.INVISIBLE
        binding.subWelcomeMessage.visibility = View.INVISIBLE
        binding.habitsRecyclerView.visibility = View.INVISIBLE
        binding.addHabit.visibility = View.INVISIBLE

        authViewModel.getUsername().observe(viewLifecycleOwner) { username ->
            binding.username.text = username
        }

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
        viewModel.habits.observe(viewLifecycleOwner) { allHabits ->
            val dayOfWeekIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            val currentDayIndex = if (dayOfWeekIndex == Calendar.SUNDAY) {
                6
            } else {
                dayOfWeekIndex - 2
            }

            val filteredHabits = allHabits.filter { it.selectedDays[currentDayIndex] }
            habitAdapter.updateData(filteredHabits)

            binding.loading.visibility = View.GONE
            binding.addHabit.visibility = View.VISIBLE
            if (filteredHabits.isEmpty()) {
                binding.welcomeMessage.visibility = View.VISIBLE
                binding.subWelcomeMessage.visibility = View.VISIBLE
                binding.habitsRecyclerView.visibility = View.GONE
            } else {
                binding.welcomeMessage.visibility = View.GONE
                binding.subWelcomeMessage.visibility = View.GONE
                binding.habitsRecyclerView.visibility = View.VISIBLE
            }
        }
    }


    private fun updateWelcomeMessageVisibility(isEmpty: Boolean) {
        if (isEmpty) {
            binding.welcomeMessage.visibility = View.VISIBLE
            binding.subWelcomeMessage.visibility = View.VISIBLE
            binding.habitsRecyclerView.visibility = View.GONE
        } else {
            binding.welcomeMessage.visibility = View.GONE
            binding.subWelcomeMessage.visibility = View.GONE
            binding.habitsRecyclerView.visibility = View.VISIBLE
        }
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
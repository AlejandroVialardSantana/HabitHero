package com.avs.habithero.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.avs.habithero.R
import com.avs.habithero.adapters.HabitAdapter
import com.avs.habithero.databinding.FragmentHomeBinding
import com.avs.habithero.models.Habit
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
    private var countDownTimer: CountDownTimer? = null
    private var isRunning = false
    private var timeLeftInMillis: Long = 0L
    private val handler = Handler(Looper.getMainLooper())

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

        val navController = findNavController()

        binding.loading.visibility = View.VISIBLE
        binding.welcomeMessage.visibility = View.GONE
        binding.subWelcomeMessage.visibility = View.GONE
        binding.habitsRecyclerView.visibility = View.GONE
        binding.addHabit.visibility = View.GONE
        binding.textView3.visibility = View.GONE

        authViewModel.getUsername().observe(viewLifecycleOwner) { username ->
            binding.textView3.text = getString(R.string.hello, username)
        }

        binding.addHabit.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddHabitFragment(null)
            navController.navigate(action)
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
            updateWelcomeMessageVisibility(filteredHabits.isEmpty())
        }
    }


    private fun updateWelcomeMessageVisibility(isEmpty: Boolean) {
        if (isEmpty) {
            binding.welcomeMessage.visibility = View.VISIBLE
            binding.subWelcomeMessage.visibility = View.VISIBLE
            binding.habitsRecyclerView.visibility = View.GONE
            binding.textView3.visibility = View.GONE
        } else {
            binding.welcomeMessage.visibility = View.GONE
            binding.subWelcomeMessage.visibility = View.GONE
            binding.habitsRecyclerView.visibility = View.VISIBLE
            binding.textView3.visibility = View.VISIBLE
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
                showDeleteConfirmationDialog(habit, position)
            },
            onCompletedClicked = { habit, isChecked ->
                val currentDate = habitAdapter.getTodayDateString()
                habit.completions[currentDate] = isChecked
                viewModel.updateHabitCompletion(habit)
            },
            onChronometerClicked = { habit ->
                showTimerDialog(habit)
            }
        )
        binding.habitsRecyclerView.adapter = habitAdapter
        binding.habitsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun showDeleteConfirmationDialog(habit: Habit, position: Int) {
        val title = getString(R.string.confirm_delete)
        val message = getString(R.string.delete_habit)
        val delete = getString(R.string.delete)
        val cancel = getString(R.string.cancel)
        AlertDialog.Builder(requireContext()).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(delete) { _, _ ->
                viewModel.deleteHabit(habit.habitId ?: "")
                habitAdapter.removeItem(position)
            }
            setNegativeButton(cancel) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
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

    private fun showTimerDialog(habit: Habit) {
        timeLeftInMillis = habit.duration * 60 * 1000L
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_timer, null)
        val timerText = dialogView.findViewById<TextView>(R.id.timerText)
        updateTimerText(timerText, timeLeftInMillis)

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(R.string.timer_dialog)
            .setView(dialogView)
            .setPositiveButton(R.string.start, null)
            .setNeutralButton(R.string.stop, null)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                cancelTimer()
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.setOnShowListener {
            val startButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val pauseButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)

            startButton.setOnClickListener {
                if (!isRunning) {
                    startTimer(timerText)
                }
            }

            pauseButton.setOnClickListener {
                if (isRunning) {
                    pauseTimer()
                }
            }

            // Actualiza el cronómetro mientras el diálogo esté abierto
            val updateTask = object : Runnable {
                override fun run() {
                    if (isRunning) {
                        updateTimerText(timerText, timeLeftInMillis)
                        handler.postDelayed(this, 1000)
                    }
                }
            }
            handler.post(updateTask)
        }

        dialog.setOnDismissListener {
            if (isRunning) {
                cancelTimer()
            }
        }

        dialog.show()
    }

    private fun startTimer(timerText: TextView) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText(timerText, millisUntilFinished)
            }

            override fun onFinish() {
                isRunning = false
                updateTimerText(timerText, 0)
            }
        }.start()
        isRunning = true
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isRunning = false
    }

    private fun cancelTimer() {
        countDownTimer?.cancel()
        isRunning = false
        timeLeftInMillis = 0L
    }

    private fun updateTimerText(timerText: TextView, millisUntilFinished: Long) {
        val hours = millisUntilFinished / 3600000
        val minutes = (millisUntilFinished % 3600000) / 60000
        val seconds = (millisUntilFinished % 60000) / 1000

        val formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        timerText.text = formattedTime
    }
}
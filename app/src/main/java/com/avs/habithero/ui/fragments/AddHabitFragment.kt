package com.avs.habithero.ui.fragments

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.avs.habithero.databinding.FragmentAddHabitBinding
import com.avs.habithero.models.Habit
import com.avs.habithero.repositories.HabitRepository
import com.avs.habithero.viewmodel.HomeViewModel
import android.widget.AdapterView
import com.avs.habithero.ui.activities.HomeActivity

class AddHabitFragment: Fragment() {

    interface CalendarActionListener {
        fun onAddEventToCalendar(habit: Habit)
    }

    private var _binding: FragmentAddHabitBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private var habitId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddHabitBinding.inflate(inflater, container, false)
        viewModel = HomeViewModel(HabitRepository())
        habitId = arguments?.getString("habitId")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeTypesSpinner()
        initializeFrequencySpinner()
        setupTimePicker()

        habitId?.let { id ->
            viewModel.getHabitById(id).observe(viewLifecycleOwner) { habit ->
                habit?.let {
                    loadHabitData(it)
                }
            }
        }

        binding.buttonSaveHabit.setOnClickListener {
            val isUpdate = habitId != null
            val habit = addOrUpdateHabit(isUpdate)
            // (activity as? HomeActivity)?.onAddEventToCalendar(habit)
            findNavController().popBackStack()
        }
    }

    private fun initializeTypesSpinner() {
        val types = resources.getStringArray(com.avs.habithero.R.array.habit_types)
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerHabitType.adapter = adapter
    }

    private fun setupTimePicker() {
        binding.timePickerNotification.setIs24HourView(true)
        binding.timePickerNotification.hour = 8
        binding.timePickerNotification.minute = 0
    }

    private fun getSelectedDays(): List<Boolean> {
        val allDays = mutableListOf<Boolean>()
        val isDaily = binding.spinnerFrequencyType.selectedItem.toString() == "Diariamente"
        allDays.add(isDaily || binding.checkboxMonday.isChecked)
        allDays.add(isDaily || binding.checkboxTuesday.isChecked)
        allDays.add(isDaily || binding.checkboxWednesday.isChecked)
        allDays.add(isDaily || binding.checkboxThursday.isChecked)
        allDays.add(isDaily || binding.checkboxFriday.isChecked)
        allDays.add(isDaily || binding.checkboxSaturday.isChecked)
        allDays.add(isDaily || binding.checkboxSunday.isChecked)
        return allDays
    }

    private fun initializeFrequencySpinner() {
        val frequencies = resources.getStringArray(com.avs.habithero.R.array.habit_frequency)
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerFrequencyType.adapter = adapter

        binding.spinnerFrequencyType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val isVisible = frequencies[position] != frequencies[0]
                binding.checkboxContainer.visibility = if (isVisible) View.VISIBLE else View.GONE

                if (!isVisible) {
                    resetCheckboxes()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.checkboxContainer.visibility = View.GONE
            }
        }
    }

    private fun addOrUpdateHabit(isUpdate: Boolean): Habit {
        val title = binding.editTextHabitTitle.text.toString()
        val type = binding.spinnerHabitType.selectedItem.toString()
        val frequencyType = binding.spinnerFrequencyType.selectedItem.toString()
        val selectedDays = getSelectedDays()
        val duration = binding.editTextDurationLabel.text.toString().toInt()
        val hour = binding.timePickerNotification.hour
        val minute = binding.timePickerNotification.minute
        val timeString = String.format("%02d:%02d", hour, minute)
        val notificationTimes = listOf(timeString)

        val habit = Habit(
            habitId = if (isUpdate) habitId else null,
            title = title,
            type = type,
            frequencyType = frequencyType,
            selectedDays = selectedDays,
            notificationTimes = notificationTimes,
            duration = duration,
        )

        if (isUpdate) {
            viewModel.updateHabit(habit, requireContext())
        } else {
            viewModel.addHabit(habit, requireContext())
        }

        return habit
    }

    private fun loadHabitData(habit: Habit) {
        binding.editTextHabitTitle.setText(habit.title)
        val typeIndex = resources.getStringArray(com.avs.habithero.R.array.habit_types).indexOf(habit.type)
        binding.spinnerHabitType.setSelection(typeIndex)
        val frequencyIndex = resources.getStringArray(com.avs.habithero.R.array.habit_frequency).indexOf(habit.frequencyType)
        binding.spinnerFrequencyType.setSelection(frequencyIndex)
        binding.editTextDurationLabel.setText(habit.duration.toString())
        binding.timePickerNotification.hour = habit.notificationTimes[0].split(":")[0].toInt()
        binding.timePickerNotification.minute = habit.notificationTimes[0].split(":")[1].toInt()
        if (habit.frequencyType != "Diariamente") {
            binding.checkboxMonday.isChecked = habit.selectedDays[0]
            binding.checkboxTuesday.isChecked = habit.selectedDays[1]
            binding.checkboxWednesday.isChecked = habit.selectedDays[2]
            binding.checkboxThursday.isChecked = habit.selectedDays[3]
            binding.checkboxFriday.isChecked = habit.selectedDays[4]
            binding.checkboxSaturday.isChecked = habit.selectedDays[5]
            binding.checkboxSunday.isChecked = habit.selectedDays[6]
        }
        else {
            resetCheckboxes()
        }
    }

    private fun resetCheckboxes() {
        binding.checkboxMonday.isChecked = false
        binding.checkboxTuesday.isChecked = false
        binding.checkboxWednesday.isChecked = false
        binding.checkboxThursday.isChecked = false
        binding.checkboxFriday.isChecked = false
        binding.checkboxSaturday.isChecked = false
        binding.checkboxSunday.isChecked = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
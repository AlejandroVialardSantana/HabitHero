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
import com.avs.habithero.model.Habit
import com.avs.habithero.repository.HabitRepository
import com.avs.habithero.viewmodel.HomeViewModel

class AddHabitFragment: Fragment() {

    private var _binding: FragmentAddHabitBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddHabitBinding.inflate(inflater, container, false)
        viewModel = HomeViewModel(HabitRepository())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeTypesSpinner()

        binding.buttonSaveHabit.setOnClickListener {
            saveHabit()
        }
    }

    private fun initializeTypesSpinner() {
        val types = resources.getStringArray(com.avs.habithero.R.array.habit_types)
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerHabitType.adapter = adapter
    }

    private fun saveHabit() {
        val title = binding.editTextHabitTitle.text.toString()
        val type = binding.spinnerHabitType.selectedItem.toString()
        val frequency = binding.seekBarFrequency.progress.toString()
        val duration = binding.textViewDurationLabel.text.toString().toInt()
        val habit = Habit(null, title, type, frequency.toInt(), duration, false)
        viewModel.addHabit(habit)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.avs.habithero.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.avs.habithero.R
import com.avs.habithero.adapters.HabitAdapter
import com.avs.habithero.databinding.FragmentHomeBinding
import com.avs.habithero.repository.HabitRepository
import com.avs.habithero.viewmodel.HomeViewModel

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

        binding.addHabit.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addHabitFragment)
        }
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
        habitAdapter = HabitAdapter(listOf())
        binding.habitsRecyclerView.adapter = habitAdapter
        binding.habitsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
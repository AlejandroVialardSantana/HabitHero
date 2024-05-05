package com.avs.habithero.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.avs.habithero.R
import com.avs.habithero.databinding.FragmentStatsBinding
import com.avs.habithero.models.Habit
import com.avs.habithero.repositories.HabitRepository
import com.avs.habithero.viewmodel.HomeViewModel
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatsFragment: Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        viewModel = HomeViewModel(HabitRepository())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeHabits()
    }

    private fun observeHabits() {
        viewModel.habits.observe(viewLifecycleOwner) { habits ->
            if (habits != null && habits.isNotEmpty()) {
                setupBarChart(habits)
            } else {
                Log.d("StatsFragment", "No se recibieron hábitos o la lista está vacía")
            }
        }
    }

    private fun setupBarChart(habits: List<Habit>) {
        val (completed, notCompleted) = calculateHabitsCompletion(habits)
        val entriesCompleted = mutableListOf<BarEntry>()
        val entriesNotCompleted = mutableListOf<BarEntry>()

        for (i in completed.indices) {
            entriesCompleted.add(BarEntry(i.toFloat(), completed[i]))
            entriesNotCompleted.add(BarEntry(i.toFloat(), notCompleted[i]))
        }

        val dataSetCompleted = BarDataSet(entriesCompleted, getString(R.string.completed))
        dataSetCompleted.color = resources.getColor(android.R.color.holo_green_light, null)
        dataSetCompleted.setDrawValues(false)

        val dataSetNotCompleted = BarDataSet(entriesNotCompleted, getString(R.string.incompleted))
        dataSetNotCompleted.color = resources.getColor(android.R.color.holo_red_light, null)
        dataSetNotCompleted.setDrawValues(false)

        val data = BarData(dataSetCompleted, dataSetNotCompleted)
        data.barWidth = 0.4f

        val daysOfWeek = listOf(
            getString(R.string.monday),
            getString(R.string.tuesday),
            getString(R.string.wednesday),
            getString(R.string.thursday),
            getString(R.string.friday),
            getString(R.string.saturday),
            getString(R.string.sunday)
        )

        binding.chartDaily.apply {
            this.data = data
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(daysOfWeek)
                axisMinimum = -0.5f
                axisMaximum = 6.5f
            }

            axisLeft.apply {
                axisMinimum = 0f
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
            }

            axisRight.isEnabled = false
            description.text = ""
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                yOffset = 12f
            }

            groupBars(-0.5f, 0.1f, 0.05f)
            invalidate()
        }
    }

    private fun calculateHabitsCompletion(habits: List<Habit>): Pair<List<Float>, List<Float>> {
        val completed = FloatArray(7)
        val notCompleted = FloatArray(7)
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = adjustDayOfWeekIndex(calendar.get(Calendar.DAY_OF_WEEK))
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (habit in habits) {
            habit.selectedDays.forEachIndexed { index, isSelected ->
                if (isSelected && index <= currentDayOfWeek) {
                    calendar.set(Calendar.DAY_OF_WEEK, index + 2)
                    val dateKey = formatter.format(calendar.time)
                    val completionStatus = habit.completions[dateKey]
                    if (completionStatus == true) {
                        completed[index]++
                    } else {
                        notCompleted[index]++
                    }
                }
            }
        }
        return completed.toList() to notCompleted.toList()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun adjustDayOfWeekIndex(dayOfWeek: Int): Int {
        return if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
    }

}
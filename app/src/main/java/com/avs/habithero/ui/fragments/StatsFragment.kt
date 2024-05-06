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
import com.avs.habithero.viewmodel.StatsViewModel
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class StatsFragment: Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StatsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        viewModel = StatsViewModel(HabitRepository())
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

    // Método que configura el gráfico de barras con los hábitos recibidos
    // El gráfico viene dado por MPAndroidChart
    private fun setupBarChart(habits: List<Habit>) {
        val (completed, notCompleted) = viewModel.calculateHabitsCompletion(habits)

        val entriesCompleted = mutableListOf<BarEntry>()
        val entriesNotCompleted = mutableListOf<BarEntry>()

        for (i in completed.indices) {
            entriesCompleted.add(BarEntry(i.toFloat(), completed[i]))
            entriesNotCompleted.add(BarEntry(i.toFloat(), notCompleted[i]))
        }

        val dataSetCompleted = createDataSet(entriesCompleted, getString(R.string.completed), android.R.color.holo_green_light)
        val dataSetNotCompleted = createDataSet(entriesNotCompleted, getString(R.string.incompleted), android.R.color.holo_red_light)

        val data = BarData(dataSetCompleted, dataSetNotCompleted)
        data.barWidth = 0.4f

        configureChartAxis()
        configureLegend()

        binding.chartDaily.apply {
            description.text = ""
            this.data = data
            groupBars(-0.5f, 0.1f, 0.05f)
            invalidate()
        }
    }

    private fun configureChartAxis() {
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
        }
    }

    private fun configureLegend() {
        binding.chartDaily.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
            yOffset = 12f
        }
    }

    private fun createDataSet(entries: List<BarEntry>, label: String, color: Int): BarDataSet {
        return BarDataSet(entries, label).apply {
            setColor(resources.getColor(color, null))
            setDrawValues(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
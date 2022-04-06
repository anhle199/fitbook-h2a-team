package com.h2a.fitbook.views.fragments.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.FragmentStatisticInWeekBinding
import com.h2a.fitbook.utils.UtilFunctions

class StatisticInWeekFragment : Fragment() {

    private var _binding: FragmentStatisticInWeekBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val colors = UtilFunctions.generateColorSet(7)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticInWeekBinding.inflate(inflater, container, false)

        setupAutoCompleteTextViewAdapter(
            binding.statisticInWeekActvExerciseIntensity,
            R.array.exercise_intensity_title
        )

        val caloriesBurnedData = arrayListOf(3750f, 2500f, 4100f, 2500f, 3250f, 4800f, 5150f)
        val caloriesBurnedBarChart = binding.statisticInWeekBchCaloriesBurned
        initBarChart(caloriesBurnedBarChart)
        showBarChart(caloriesBurnedBarChart, caloriesBurnedData)

        val workoutTimesData = arrayListOf(65f, 40f, 73f, 40f, 55f, 88f, 94f)
        val workoutTimesBarChart = binding.statisticInWeekBchWorkoutTimes
        initBarChart(workoutTimesBarChart)
        showBarChart(workoutTimesBarChart, workoutTimesData)

        return binding.root
    }

    private fun setupAutoCompleteTextViewAdapter(
        view: AutoCompleteTextView,
        optionsResourceId: Int,
        defaultIndex: Int = 0
    ) {
        if (context != null) {
            val safeContext = requireContext()

            // Set adapter for AutoCompleteTextView
            val options = resources.getStringArray(optionsResourceId)
            ArrayAdapter(
                safeContext,
                android.R.layout.simple_spinner_dropdown_item,
                options
            ).also {
                view.setAdapter(it)
            }

            // Set default option for this
            if (defaultIndex >= 0 && defaultIndex < options.size) {
                view.setText(options[defaultIndex], false)
            }
        }
    }

    private fun initBarChart(barChart: BarChart) {
        barChart.description.isEnabled = false
        barChart.animateY(800, Easing.EaseInOutQuad)

        // Actions
        barChart.isHighlightPerTapEnabled = false
        barChart.isDoubleTapToZoomEnabled = false

        barChart.setDrawValueAboveBar(true)
        barChart.setDrawGridBackground(false)

        // Axis
//        barChart.axisRight.isEnabled = false
        barChart.xAxis.isEnabled = false
//        barChart.xAxis.textSize = 8f
//        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(resources.getStringArray(R.array.daysOfWeek))
//        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        // No data
        barChart.setNoDataText("Không đủ dữ liệu để thống kê.")
        barChart.setNoDataTextColor(R.color.black)
        barChart.setNoDataTextTypeface(resources.getFont(R.font.roboto_medium))

        // Space between chart and legend.
        barChart.extraBottomOffset = 12f

//        barChart.legend.isEnabled = false
        val legend = barChart.legend
        legend.isWordWrapEnabled = true
        legend.xEntrySpace = 12f
        legend.yEntrySpace = 4f
        legend.form = Legend.LegendForm.SQUARE
        legend.formToTextSpace = 4f
        legend.formSize = 12f
        legend.textSize = 12f

        val daysOfWeek = resources.getStringArray(R.array.days_of_week)
        legend.setCustom(daysOfWeek.mapIndexedTo(ArrayList()) { index, value ->
            val entry = LegendEntry()
            entry.label = value
            entry.formColor = colors[index]

            return@mapIndexedTo entry
        })
    }

    private fun showBarChart(barChart: BarChart, rawData: ArrayList<Float>) {
        val barEntries = rawData.mapIndexedTo(ArrayList()) { index, value ->
            BarEntry(index.toFloat(), value)
        }

        val dataSet = BarDataSet(barEntries, "")
        dataSet.valueTextSize = 10f
        dataSet.valueTextColor = resources.getColor(R.color.dark_gray, null)
        dataSet.valueTypeface = resources.getFont(R.font.roboto)
        dataSet.colors = colors

        val barData = BarData(dataSet)
        barData.setDrawValues(true)

        barChart.data = barData
        barChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.h2a.fitbook.views.fragments.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.FragmentStatisticInDateBinding
import com.h2a.fitbook.utils.UtilFunctions
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class StatisticInDateFragment : Fragment() {

    private var _binding: FragmentStatisticInDateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val colors = UtilFunctions.generateColorSet()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticInDateBinding.inflate(inflater, container, false)

        // Set default value is today
        // Monday -> Sunday <==> 1 -> 7
        val day = LocalDate.now().dayOfWeek.value

        setupAutoCompleteTextViewAdapter(
            binding.statisticInDateActvStatisticDate,
            R.array.days_of_week,
            day - 1
        )
        setupAutoCompleteTextViewAdapter(
            binding.statisticInDateActvExerciseIntensity,
            R.array.exercise_intensity_title
        )

        val caloriesBurnedData = LinkedHashMap<String, Float>()
        caloriesBurnedData["Bài tập 1"] = 1000f
        caloriesBurnedData["Bài tập 2"] = 1250f
        caloriesBurnedData["Bài tập 3"] = 1000f
        caloriesBurnedData["Bài tập 4"] = 1750f
        val caloriesBurnedPieChart = binding.statisticInDatePchCaloriesBurned
        initPieChart(caloriesBurnedPieChart, resources.getString(R.string.statistic_calories_burned_chart_unit))
        showPieChart(caloriesBurnedPieChart, caloriesBurnedData)

        val workoutTimesData = LinkedHashMap<String, Float>()
        workoutTimesData["Bài tập 1"] = 5f
        workoutTimesData["Bài tập 2"] = 12.5f
        workoutTimesData["Bài tập 3"] = 7.5f
        workoutTimesData["Bài tập 4"] = 5f
        val workoutTimesPieChart = binding.statisticInDatePchWorkoutTimes
        initPieChart(workoutTimesPieChart, resources.getString(R.string.statistic_workout_times_chart_unit))
        showPieChart(workoutTimesPieChart, workoutTimesData)

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

    private fun initPieChart(pieChart: PieChart, unit: String) {
        pieChart.description.isEnabled = false
        pieChart.setDrawEntryLabels(false)  // hide entry text

        pieChart.rotationAngle = 0f
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(800, Easing.EaseInOutQuad)

        // Center text (inside circle)
        pieChart.centerText = unit
        pieChart.setCenterTextSize(14f)
        pieChart.setCenterTextTypeface(resources.getFont(R.font.roboto_medium))

        // No data
        pieChart.setNoDataText("Không đủ dữ liệu để thống kê.")
        pieChart.setNoDataTextColor(R.color.black)
        pieChart.setNoDataTextTypeface(resources.getFont(R.font.roboto_medium))

        // Legend
        val legend = pieChart.legend
        legend.isWordWrapEnabled = true
        legend.xEntrySpace = 12f
        legend.yEntrySpace = 4f
        legend.form = Legend.LegendForm.CIRCLE
        legend.formToTextSpace = 4f
        legend.formSize = 12f
        legend.textSize = 12f
    }

    private fun showPieChart(pieChart: PieChart, rawData: LinkedHashMap<String, Float>) {
        val pieEntries = rawData.mapTo(ArrayList()) {
            PieEntry(it.value, it.key)
        }

        val dataSet = PieDataSet(pieEntries, "")
        dataSet.valueTextSize = if (pieEntries.size > 8) 12f else 14f
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTypeface = resources.getFont(R.font.roboto_medium)
        dataSet.colors = colors

        val pieData = PieData(dataSet)
        pieData.setDrawValues(true)  // show value of each entry

        pieChart.data = pieData
        pieChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

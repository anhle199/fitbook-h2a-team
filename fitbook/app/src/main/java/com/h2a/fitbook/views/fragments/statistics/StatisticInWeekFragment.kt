package com.h2a.fitbook.views.fragments.statistics

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.h2a.fitbook.viewmodels.statistics.StatisticInWeekViewModel
import com.h2a.fitbook.viewmodels.statistics.StatisticsViewModel

class StatisticInWeekFragment : Fragment() {

    private var _binding: FragmentStatisticInWeekBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: StatisticInWeekViewModel

    private val colors = UtilFunctions.generateColorSet(7)
    private var exerciseIntensityTitles = ArrayList<String>()
    private var exerciseIntensityValues = ArrayList<Float>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticInWeekBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[StatisticInWeekViewModel::class.java]
        viewModel.isFetching.observe(this.viewLifecycleOwner) { isFetching ->
            binding.statisticInWeekActvExerciseIntensity.isEnabled = !isFetching
            setEnabledForFABs(!isFetching)

            if (isFetching) {
                val parentViewModel = ViewModelProvider(requireActivity())[StatisticsViewModel::class.java]
                refreshStatisticValues(parentViewModel.isRefreshing.value!!)
            }
        }

        exerciseIntensityTitles.addAll(resources.getStringArray(R.array.exercise_intensity_titles))
        exerciseIntensityValues.addAll(
            resources.getStringArray(R.array.exercise_intensity_values).map {
                it.toFloat()
            }
        )

        addActionForFABs()

        // Set up adapter and add text changed listener for AutoCompleteTextView
        setUpExerciseIntensityDropdown()

        // Init bar charts
        initBarChart(binding.statisticInWeekBchCaloriesBurned)
        initBarChart(binding.statisticInWeekBchWorkoutTimes)

        Handler(Looper.getMainLooper())
            .postDelayed({
                executeStatistic()
            }, 3000)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        viewModel.setFetchingState(true)
    }

    private fun addActionForFABs() {
        // Set on click listener for Floating Button Menu
        val menuFab = binding.statisticInWeekFabMenu
        menuFab.setOnClickListener {
            if (this.context != null) {
                val safeContext = this.requireContext()

                val visibility = if (binding.statisticInWeekFabDownload.isVisible) View.INVISIBLE else View.VISIBLE
//                binding.statisticInWeekFabShare.visibility = visibility
                binding.statisticInWeekFabDownload.visibility = visibility

                if (visibility == View.VISIBLE) {
                    menuFab.backgroundTintList = ContextCompat.getColorStateList(safeContext, R.color.fab_close_color)
                    menuFab.setImageResource(R.drawable.ic_round_close_24)
                } else {
                    menuFab.backgroundTintList = ContextCompat.getColorStateList(safeContext, R.color.primary)
                    menuFab.setImageResource(R.drawable.ic_round_menu_24)
                }
            }
        }

        binding.statisticInWeekFabDownload.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request write external storage permission
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,), 1)
            } else {
                Toast.makeText(context, "Biểu đồ đầu tiên đang được lưu vào thư viện.", Toast.LENGTH_SHORT).show()
                val caloriesStatsBitmap = UtilFunctions.convertViewToBitmap(binding.statisticInWeekCvCaloriesStats)
                val isSavedFirstBitmap = UtilFunctions.saveBitmapToGallery(caloriesStatsBitmap)
                if (isSavedFirstBitmap) {
                    Toast.makeText(context, "Đã lưu thành công biểu đồ đầu tiên.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Lưu không thành công biểu đồ đầu tiên.", Toast.LENGTH_SHORT).show()
                }

                Toast.makeText(context, "Biểu đồ thứ hai đang được lưu vào thư viện.", Toast.LENGTH_SHORT).show()
                val workoutTimesStatsBitmap = UtilFunctions.convertViewToBitmap(binding.statisticInWeekCvWorkoutTimesStats)
                val isSavedSecondBitmap = UtilFunctions.saveBitmapToGallery(workoutTimesStatsBitmap)
                if (isSavedSecondBitmap) {
                    Toast.makeText(context, "Đã lưu thành công biểu đồ thứ hai.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Lưu không thành công biểu đồ thứ hai.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setEnabledForFABs(isEnabled: Boolean) {
        binding.statisticInWeekFabMenu.isEnabled = isEnabled
        binding.statisticInWeekFabDownload.isEnabled = isEnabled
        binding.statisticInWeekFabShare.isEnabled = isEnabled
    }

    private fun setUpExerciseIntensityDropdown() {
        if (context != null) {
            val safeContext = requireContext()
            val exerciseIntensityDropdown = binding.statisticInWeekActvExerciseIntensity

            // Set adapter for AutoCompleteTextView
            ArrayAdapter(
                safeContext,
                android.R.layout.simple_spinner_dropdown_item,
                exerciseIntensityTitles
            ).also { exerciseIntensityDropdown.setAdapter(it) }

            // Set default option for this
            val defaultIndex = 0
            exerciseIntensityDropdown.setText(exerciseIntensityTitles[defaultIndex], false)
            viewModel.exerciseIntensity = exerciseIntensityValues[defaultIndex]

            exerciseIntensityDropdown.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val index = exerciseIntensityTitles.indexOfFirst { it.contentEquals(text) }
                    if (index != -1) {
                        viewModel.exerciseIntensity = exerciseIntensityValues[index]
                        binding.statisticInWeekTvStandardCaloriesValue.text = viewModel.standardCalories.toString()
                    }

                    Log.i("StatisticByWeek", "Exercise intensity index: $index")
                    Log.i("StatisticByWeek", "Exercise intensity: $text")
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
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

    private fun setStatisticSpecifications() {
        // First chart
        binding.statisticInWeekTvStandardCaloriesValue.text = UtilFunctions.convertFloatToFormattedString(viewModel.standardCalories)
        binding.statisticInWeekTvCaloriesConsumedValue.text = UtilFunctions.convertFloatToFormattedString(viewModel.consumedCalories)
        binding.statisticInWeekTvCaloriesBurnedValue.text = UtilFunctions.convertFloatToFormattedString(viewModel.burnedCalories)

        // Second chart
        binding.statisticInWeekTvNumberOfExercisesValue.text = viewModel.exerciseCount.toString()
        binding.statisticInWeekTvWorkoutTimesValue.text = UtilFunctions.convertFloatToFormattedString(viewModel.totalWorkoutTime)
    }

    private fun setBarChartData(barChart: BarChart, rawData: ArrayList<Float>) {
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

    private fun executeStatistic() {
        setStatisticSpecifications()
        setBarChartData(binding.statisticInWeekBchCaloriesBurned, viewModel.caloriesBurnedData)
        setBarChartData(binding.statisticInWeekBchWorkoutTimes, viewModel.workoutTimesData)
    }

    private fun refreshStatisticValues(isRefreshing: Boolean) {
        Log.i("StatisticByWeek", "Starts Refreshing")
        val parentViewModel = ViewModelProvider(requireActivity())[StatisticsViewModel::class.java]
        if (!isRefreshing) {
            parentViewModel.setRefreshingState(true)
        }

        viewModel.fetchData()

        Handler(Looper.getMainLooper())
            .postDelayed({
                viewModel.setFetchingState(false)
                executeStatistic()
                parentViewModel.setRefreshingState(false)
                Log.i("StatisticByWeek", "Refresh Completed")
            }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

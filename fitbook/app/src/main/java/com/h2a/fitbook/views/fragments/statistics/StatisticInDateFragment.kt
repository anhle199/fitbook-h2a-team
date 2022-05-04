package com.h2a.fitbook.views.fragments.statistics

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.FragmentStatisticInDateBinding
import com.h2a.fitbook.utils.UtilFunctions
import com.h2a.fitbook.viewmodels.statistics.StatisticInDateViewModel
import com.h2a.fitbook.viewmodels.statistics.StatisticsViewModel
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class StatisticInDateFragment : Fragment() {

    private var _binding: FragmentStatisticInDateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: StatisticInDateViewModel

    private val colors = UtilFunctions.generateColorSet()
    private var daysOfWeek = ArrayList<String>()
    private var exerciseIntensityTitles = ArrayList<String>()
    private var exerciseIntensityValues = ArrayList<Float>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticInDateBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[StatisticInDateViewModel::class.java]
        viewModel.isFetching.observe(this.viewLifecycleOwner) { isFetching ->
            binding.statisticInDateActvStatisticDate.isEnabled = !isFetching
            binding.statisticInDateActvExerciseIntensity.isEnabled = !isFetching
            setEnabledForFABs(!isFetching)

            if (isFetching) {
                val parentViewModel = ViewModelProvider(requireActivity())[StatisticsViewModel::class.java]
                refreshStatisticValues(parentViewModel.isRefreshing.value!!)
            }
        }

        daysOfWeek.addAll(resources.getStringArray(R.array.days_of_week))
        exerciseIntensityTitles.addAll(resources.getStringArray(R.array.exercise_intensity_titles))
        exerciseIntensityValues.addAll(
            resources.getStringArray(R.array.exercise_intensity_values).map {
                it.toFloat()
            }
        )

        addActionForFABs()

        // Set up adapters and add text changed listeners for two AutoCompleteTextView
        setUpStatisticDateDropdown()
        setUpExerciseIntensityDropdown()

        // Init pie charts
        initPieChart(binding.statisticInDatePchCaloriesBurned, resources.getString(R.string.statistic_calories_burned_chart_unit))
        initPieChart(binding.statisticInDatePchWorkoutTimes, resources.getString(R.string.statistic_workout_times_chart_unit))

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        viewModel.setFetchingState(true)
    }

    private fun addActionForFABs() {
        // Set on click listener for Floating Button Menu
        val menuFab = binding.statisticInDateFabMenu
        menuFab.setOnClickListener {
            if (this.context != null) {
                val safeContext = this.requireContext()

                val visibility = if (binding.statisticInDateFabShare.isVisible) View.INVISIBLE else View.VISIBLE
                binding.statisticInDateFabShare.visibility = visibility
                binding.statisticInDateFabDownload.visibility = visibility

                if (visibility == View.VISIBLE) {
                    menuFab.backgroundTintList = ContextCompat.getColorStateList(safeContext, R.color.fab_close_color)
                    menuFab.setImageResource(R.drawable.ic_round_close_24)
                } else {
                    menuFab.backgroundTintList = ContextCompat.getColorStateList(safeContext, R.color.primary)
                    menuFab.setImageResource(R.drawable.ic_round_menu_24)
                }
            }
        }

        binding.statisticInDateFabDownload.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request write external storage permission
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                Toast.makeText(context, "Biểu đồ đầu tiên đang được lưu vào thư viện.", Toast.LENGTH_SHORT).show()
                val caloriesStatsBitmap = UtilFunctions.convertViewToBitmap(binding.statisticInDateCvCaloriesStats)
                val isSavedFirstBitmap = UtilFunctions.saveBitmapToGallery(caloriesStatsBitmap)
                if (isSavedFirstBitmap) {
                    Toast.makeText(context, "Đã lưu thành công biểu đồ đầu tiên.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Lưu không thành công biểu đồ đầu tiên.", Toast.LENGTH_SHORT).show()
                }

                Toast.makeText(context, "Biểu đồ thứ hai đang được lưu vào thư viện.", Toast.LENGTH_SHORT).show()
                val workoutTimesStatsBitmap = UtilFunctions.convertViewToBitmap(binding.statisticInDateCvWorkoutTimesStats)
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
        binding.statisticInDateFabMenu.isEnabled = isEnabled
        binding.statisticInDateFabDownload.isEnabled = isEnabled
        binding.statisticInDateFabShare.isEnabled = isEnabled
    }

    private fun setUpStatisticDateDropdown() {
        if (context != null) {
            val safeContext = requireContext()
            val statisticDateDropdown = binding.statisticInDateActvStatisticDate

            // Set adapter for AutoCompleteTextView
            ArrayAdapter(
                safeContext,
                android.R.layout.simple_spinner_dropdown_item,
                daysOfWeek
            ).also { statisticDateDropdown.setAdapter(it) }

            // Set default option for this
            // Default value is today's value
            // Monday -> Sunday <==> 1 -> 7
            val defaultIndex = LocalDate.now().dayOfWeek.value - 1
            statisticDateDropdown.setText(daysOfWeek[defaultIndex], false)
            viewModel.selectedDateIndex = defaultIndex

            statisticDateDropdown.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val index = daysOfWeek.indexOfFirst { it.contentEquals(text) }
                    if (index != -1) {
                        viewModel.selectedDateIndex = index
                        viewModel.setFetchingState(true)
                    }

                    Log.i("StatisticByDate", "Day of week index: $index")
                    Log.i("StatisticByDate", "Day of week: $text")
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
        }
    }

    private fun setUpExerciseIntensityDropdown() {
        if (context != null) {
            val safeContext = requireContext()
            val exerciseIntensityDropdown = binding.statisticInDateActvExerciseIntensity

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
                        binding.statisticInDateTvStandardCaloriesValue.text = viewModel.standardCalories.toString()
                    }

                    Log.i("StatisticByDate", "Exercise intensity index: $index")
                    Log.i("StatisticByDate", "Exercise intensity: $text")
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
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

    private fun setStatisticSpecifications() {
        // First chart
        binding.statisticInDateTvStandardCaloriesValue.text = UtilFunctions.convertFloatToFormattedString(viewModel.standardCalories)
        binding.statisticInDateTvCaloriesConsumedValue.text = UtilFunctions.convertFloatToFormattedString(viewModel.consumedCalories)
        binding.statisticInDateTvCaloriesBurnedValue.text = UtilFunctions.convertFloatToFormattedString(viewModel.burnedCalories)

        // Second chart
        binding.statisticInDateTvNumberOfExercisesValue.text = viewModel.exerciseCount.toString()
        binding.statisticInDateTvWorkoutTimesValue.text = UtilFunctions.convertFloatToFormattedString(viewModel.totalWorkoutTime)
    }

    private fun setPieChartData(pieChart: PieChart, rawData: LinkedHashMap<String, Float>) {
        var pieData: PieData? = null

        if (rawData.isNotEmpty()) {
            val pieEntries = rawData.mapTo(ArrayList()) {
                PieEntry(it.value, it.key)
            }

            val dataSet = PieDataSet(pieEntries, "")
            dataSet.valueTextSize = if (pieEntries.size > 8) 12f else 14f
            dataSet.valueTextColor = Color.WHITE
            dataSet.valueTypeface = resources.getFont(R.font.roboto_medium)
            dataSet.colors = colors

            pieData = PieData(dataSet)
            pieData.setDrawValues(true)  // show value of each entry
        }

        pieChart.data = pieData
        pieChart.invalidate()
    }

    private fun executeStatistic() {
        setStatisticSpecifications()
        setPieChartData(binding.statisticInDatePchCaloriesBurned, viewModel.caloriesBurnedData)
        setPieChartData(binding.statisticInDatePchWorkoutTimes, viewModel.workoutTimesData)
    }

    private fun refreshStatisticValues(isRefreshing: Boolean) {
        Log.i("StatisticByDate", "Starts Refreshing")
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
                Log.i("StatisticByDate", "Refresh Completed")
            }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

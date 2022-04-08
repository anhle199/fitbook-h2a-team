package com.h2a.fitbook.views.activities.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityHomeRescheduleBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeRescheduleActivity : AppCompatActivity() {
    private var _binding: ActivityHomeRescheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeRescheduleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.homeRescheduleImgThumbnail.load(intent.getStringExtra("EXERCISE_THUMBNAIL")) {
            placeholder(R.drawable.bg_placeholder)
        }
        binding.homeRescheduleTvTitle.text = intent.getStringExtra("EXERCISE_TITLE")
        binding.homeRescheduleTvDetail.text = intent.getStringExtra("EXERCISE_DETAIL")

        // Choose time
        binding.homeRescheduleEtTime.setOnClickListener {
            setupAndShowTimePicker()
        }

        // Choose date
        binding.homeRescheduleEtDate.setOnClickListener {
            setupAndShowDatePicker()
        }

        binding.homeRescheduleBtnSave.setOnClickListener { // Save and go back
            finish()
        }

        supportActionBar?.let {
            it.show()
            it.title = resources.getString(R.string.title_activity_home_reschedule)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {  // Back button action
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupAndShowDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(resources.getString(R.string.activity_home_reschedule_tv_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat(resources.getString(R.string.date_format))
            binding.homeRescheduleEtDate.setText(dateFormatter.format(it))
        }

        datePicker.show(supportFragmentManager, "date_selection")
    }

    @SuppressLint("SetTextI18n")
    private fun setupAndShowTimePicker() {
        val currentTime = Calendar.getInstance()
        val curHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val curMinute = currentTime.get(Calendar.MINUTE)

        val timePicker = MaterialTimePicker.Builder().setHour(curHour).setMinute(curMinute)
            .setTitleText(resources.getString(R.string.activity_home_reschedule_tv_time))
            .setInputMode(INPUT_MODE_CLOCK).build()

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            binding.homeRescheduleEtTime.setText("$hour:$minute")
        }
        timePicker.show(supportFragmentManager, "time_selection")
    }
}
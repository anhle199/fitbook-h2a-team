package com.h2a.fitbook.views.activities.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityHomeRescheduleBinding
import com.h2a.fitbook.viewmodels.home.ExerciseDetailViewModel
import com.h2a.fitbook.viewmodels.home.HomeRescheduleViewModel
import com.h2a.fitbook.views.activities.LoginActivity
import com.h2a.fitbook.views.activities.schedule.ScheduleDetailActivity
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HomeRescheduleActivity : AppCompatActivity() {
    private var _binding: ActivityHomeRescheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeRescheduleViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            return
        }

        _binding = ActivityHomeRescheduleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.homeRescheduleImgThumbnail.load(intent.getStringExtra("EXERCISE_THUMBNAIL")) {
            placeholder(R.drawable.bg_placeholder)
        }
        binding.homeRescheduleTvTitle.text = intent.getStringExtra("EXERCISE_TITLE")

        viewModel.name = intent.getStringExtra("EXERCISE_TITLE").toString()
        viewModel.id = intent.getStringExtra("EXERCISE_ID").toString()
        viewModel.measureDuration = intent.getIntExtra("EXERCISE_DURATION", 0)
        viewModel.measureCalories = intent.getStringExtra("EXERCISE_CALORIES").toString().toDouble()
        viewModel.mode = intent.getStringExtra("EXERCISE_MODE") ?: "CREATE"

        binding.homeRescheduleTvDetail.text =
            "${viewModel.measureDuration / 60} Phút | ${viewModel.measureCalories} Calo"

        // focus duration
        binding.homeRescheduleEtDuration.requestFocus()

        // Choose time
        binding.homeRescheduleEtTime.setOnClickListener {
            setupAndShowTimePicker()
        }

        // Choose date
        binding.homeRescheduleEtDate.setOnClickListener {
            if (viewModel.mode == "CREATE") {
                setupAndShowDatePicker()
            }
        }

        // map data in edit mode
        if (viewModel.mode == "EDIT") {
            val scheduleDate = intent.getStringExtra("EXERCISE_DATE").toString()
            val date =
                LocalDateTime.parse(scheduleDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME).format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            binding.homeRescheduleEtDate.setText(date)

            val time =
                LocalDateTime.parse(scheduleDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME).format(
                    DateTimeFormatter.ofPattern("HH:mm")
                )
            binding.homeRescheduleEtTime.setText(time)
            binding.homeRescheduleEtSet.setText("${intent.getLongExtra("EXERCISE_SET", 0)}")
            binding.homeRescheduleEtDuration.setText(
                "${
                    intent.getLongExtra(
                        "EXERCISE_MEASURE_DURATION", 0
                    ) / 60
                }"
            )

            viewModel.oldSet = intent.getLongExtra("EXERCISE_SET", 0).toString().toInt()
            viewModel.oldDuration =
                intent.getLongExtra("EXERCISE_MEASURE_DURATION", 0).toString().toInt() / 60
        }

        binding.homeRescheduleBtnSave.setOnClickListener {
            val duration = binding.homeRescheduleEtDuration.text
            val set = binding.homeRescheduleEtSet.text
            val date = binding.homeRescheduleEtDate.text
            val time = binding.homeRescheduleEtTime.text
            if (duration?.isEmpty() == true || set?.isEmpty() == true || date?.isEmpty() == true || time?.isEmpty() == true) {
                Toast.makeText(
                    this, R.string.activity_home_reschedule_save_alert, Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            viewModel.duration = duration.toString().toInt()
            viewModel.set = set.toString().toInt()
            viewModel.date = date.toString()
            viewModel.time = time.toString()

            if (viewModel.mode == "CREATE") {
                viewModel.scheduleAnExercise {
                    if (it) {
                        Toast.makeText(
                            this,
                            R.string.activity_home_reschedule_save_alert_ok,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            R.string.activity_home_reschedule_save_alert_ok,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else { // edit here
                viewModel.editAnExercise {
                    if (it) {
                        Toast.makeText(
                            this,
                            R.string.activity_home_reschedule_save_alert_ok,
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent()
                        intent.putExtra("RESCHEDULE_DURATION", duration.toString().toLong() * 60)
                        intent.putExtra("RESCHEDULE_SET", set.toString().toLong())
                        intent.putExtra(
                            "RESCHEDULE_TIME",
                            LocalDateTime.parse(
                                "$date $time",
                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                            ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        )
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            R.string.activity_home_reschedule_save_alert_ok,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
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
        val constraintsBuilder =
            CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraintsBuilder.build())
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
            val hourStr = if (hour < 10) {
                "0$hour"
            } else "$hour"

            val minuteStr = if (minute < 10) {
                "0$minute"
            } else "$minute"

            binding.homeRescheduleEtTime.setText("$hourStr:$minuteStr")
        }
        timePicker.show(supportFragmentManager, "time_selection")
    }
}
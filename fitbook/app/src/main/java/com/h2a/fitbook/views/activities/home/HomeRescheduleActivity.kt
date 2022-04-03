package com.h2a.fitbook.views.activities.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityHomeRescheduleBinding
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

        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        // Choose time
        val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minuteOfHour ->
            val timePicked = "$hourOfDay:$minuteOfHour"
            binding.homeRescheduleEtTime.setText(timePicked)
        }, hour, minute, true)
        binding.homeRescheduleEtTime.setOnClickListener {
            timePickerDialog.show()
        }

        // Choose date
        val datePickerDialog = DatePickerDialog(this)
        datePickerDialog.setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val datePicked = "$dayOfMonth/${monthOfYear + 1}/$year"
            binding.homeRescheduleEtDate.setText(datePicked)
        }
        binding.homeRescheduleEtDate.setOnClickListener {
            datePickerDialog.show()
        }

        binding.homeRescheduleBtnSave.setOnClickListener { // Save and go back
            val intent = Intent(this, HomeDetailActivity::class.java)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
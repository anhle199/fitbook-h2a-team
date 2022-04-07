package com.h2a.fitbook.views.activities.schedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityScheduleTimerBinding

class ScheduleTimerActivity : AppCompatActivity() {
    private var _binding: ActivityScheduleTimerBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScheduleTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.let {
            it.show()
            it.title = resources.getString(R.string.schedule_time_title)
            it.setDisplayHomeAsUpEnabled(true)
        }

        binding.scheduleTimerTvTitle.text = ""
        binding.scheduleTimerTvDetail.text = ""
        binding.scheduleTimerTvTime.text = "00:02:00"

        binding.scheduleTimerFabReplay.setOnClickListener {}

        binding.scheduleTimerFabPlay.setOnClickListener {
            it.visibility = View.GONE
            binding.scheduleTimerFabPause.visibility = View.VISIBLE
        }

        binding.scheduleTimerFabPause.setOnClickListener {
            it.visibility = View.GONE
            binding.scheduleTimerFabPlay.visibility = View.VISIBLE
        }
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
}
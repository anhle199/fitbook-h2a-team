package com.h2a.fitbook.views.activities.schedule

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.schedule.ScheduleDailyListAdapter
import com.h2a.fitbook.databinding.ActivityScheduleDailyBinding
import com.h2a.fitbook.viewmodels.schedule.ScheduleDailyViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleDailyActivity : AppCompatActivity() {
    private var _binding: ActivityScheduleDailyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScheduleDailyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScheduleDailyBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        val activityTitle = resources.getString(R.string.menu_schedule)

        viewModel.currentDate = intent.getStringExtra("SCHEDULE_DAILY_DATE").toString()
        val dayOfWeekString = if (LocalDate.now().dayOfWeek == LocalDate.parse(
                viewModel.currentDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")
            ).dayOfWeek
        ) {
            resources.getString(R.string.schedule_list_item_tv_date)
        } else {
            viewModel.currentDate
        }
        supportActionBar?.let {
            it.show()
            it.title = "$activityTitle - $dayOfWeekString"
            it.setDisplayHomeAsUpEnabled(true)
        }

        binding.scheduleDailyClLoading.visibility = View.VISIBLE
        viewModel.getScheduleDailyList { isOk, data ->
            binding.scheduleDailyClLoading.visibility = View.GONE
            if (isOk) {
                val sortedData = data.sortedBy {
                    LocalDateTime.parse(
                        it.scheduleDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    )
                }
                val adapter =
                    ScheduleDailyListAdapter(ArrayList(sortedData))
                adapter.onItemClick = {
                    val intent = Intent(this, ScheduleDetailActivity::class.java)
                    intent.putExtra("SCHEDULE_DAILY_EXERCISE_ID", it.id)
                    intent.putExtra("SCHEDULE_DAILY_SCHEDULE_DATE", it.scheduleDate)
                    intent.putExtra("SCHEDULE_DAILY_EXERCISE_DURATION", it.duration)
                    intent.putExtra("SCHEDULE_DAILY_EXERCISE_CALORIES", it.measureCalories)
                    intent.putExtra("SCHEDULE_DAILY_EXERCISE_TOTAL_SET", it.totalSet)
                    intent.putExtra("SCHEDULE_DAILY_EXERCISE_ACTUAL_SET", it.actualSet)
                    startActivity(intent)
                }
                binding.scheduleDailyRcvList.adapter = adapter
                binding.scheduleDailyRcvList.layoutManager = LinearLayoutManager(this)
            } else {
                Toast.makeText(this, R.string.schedule_load_error, Toast.LENGTH_SHORT).show()
            }
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
}
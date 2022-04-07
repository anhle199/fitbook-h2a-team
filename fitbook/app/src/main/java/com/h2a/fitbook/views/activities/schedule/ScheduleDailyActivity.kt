package com.h2a.fitbook.views.activities.schedule

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.schedule.ScheduleDailyListAdapter
import com.h2a.fitbook.databinding.ActivityScheduleDailyBinding
import com.h2a.fitbook.models.ExerciseDailyModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleDailyActivity : AppCompatActivity() {
    private var _binding: ActivityScheduleDailyBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScheduleDailyBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        val activityTitle = resources.getString(R.string.menu_schedule)
        val selectedDate = intent.getStringExtra("SCHEDULE_DAILY_DATE")
        val dayOfWeekString = if (LocalDate.now().dayOfWeek == LocalDate.parse(
                selectedDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")
            ).dayOfWeek
        ) {
            resources.getString(R.string.schedule_list_item_tv_date)
        } else {
            selectedDate
        }
        supportActionBar?.let {
            it.show()
            it.title = "$activityTitle - $dayOfWeekString"
            it.setDisplayHomeAsUpEnabled(true)
        }

        val exerciseList = arrayListOf<ExerciseDailyModel>()
        for (i in 1..9) {
            exerciseList.add(
                ExerciseDailyModel(
                    i.toString(),
                    "Bài $i",
                    2,
                    50,
                    "https://i.ibb.co/qg4Pjx4/bg-home-list-item-running.png",
                    "",
                    arrayListOf(),
                    10,
                    250,
                    "2022-04-07T0$i:00:00.000"
                )
            )
        }
        val adapter = ScheduleDailyListAdapter(exerciseList)
        adapter.onItemClick = {
            val intent = Intent(this, ScheduleDetailActivity::class.java)
            intent.putExtra("SCHEDULE_DAILY_EXERCISE_ID", it.id)
            startActivity(intent)
        }
        binding.scheduleDailyRcvList.adapter = adapter
        binding.scheduleDailyRcvList.layoutManager = LinearLayoutManager(this)
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
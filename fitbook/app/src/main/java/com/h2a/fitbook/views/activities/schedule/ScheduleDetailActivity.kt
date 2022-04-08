package com.h2a.fitbook.views.activities.schedule

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.home.ExerciseStepListAdapter
import com.h2a.fitbook.databinding.ActivityScheduleDetailBinding
import com.h2a.fitbook.models.ExerciseDailyModel
import com.h2a.fitbook.shared.dialogs.ConfirmDialog
import com.h2a.fitbook.views.activities.home.HomeRescheduleActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleDetailActivity : AppCompatActivity() {
    private var _binding: ActivityScheduleDetailBinding? = null
    private val binding get() = _binding!!

    private var isOpenFloatingContainer: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScheduleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.let {
            it.show()
            it.title = resources.getString(R.string.title_activity_home_detail)
            it.setDisplayHomeAsUpEnabled(true)
        }

        binding.scheduleDetailFabMenu.setOnClickListener {
            if (!isOpenFloatingContainer) {
                openOverlay()
            } else {
                closeOverlay()
            }
        }

        binding.scheduleDetailFabDelete.setOnClickListener {
            val dialog = ConfirmDialog(
                resources.getString(R.string.schedule_detail_delete_dialog_title),
                resources.getString(R.string.schedule_detail_delete_dialog_description)
            )
            dialog.show(supportFragmentManager, "confirm")
            dialog.onButtonClick = {
                if (it) {
                    closeOverlay()
                    finish()
                }
            }
        }

        binding.scheduleDetailFabStart.setOnClickListener {
            closeOverlay()
            val intent = Intent(this, ScheduleTimerActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        // get exercise by id from API
        val exerciseId = intent.getStringExtra("SCHEDULE_DAILY_EXERCISE_ID")
        val steps = arrayListOf<String>()
        for (i in 1..5) {
            steps.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Id sed vitae diam morbi ut eget dolor. Suspendisse dictum ipsum risus sodales nisi id.")
        }
        val exercise = ExerciseDailyModel(
            "1",
            "Bài 1",
            2,
            50,
            "https://i.ibb.co/qg4Pjx4/bg-home-list-item-running.png",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Id sed vitae diam morbi ut eget dolor. Suspendisse dictum ipsum risus sodales nisi id.",
            steps,
            10,
            250,
            "2022-04-07T07:00:00.000"
        )

        binding.scheduleDetailImgThumbnail.load(exercise.image) {
            placeholder(R.drawable.bg_placeholder)
        }
        binding.scheduleDetailTvTitle.text = exercise.title
        binding.scheduleDetailTvDetail.text =
            "${exercise.totalDuration} Phút | ${exercise.totalCalories} Calo"
        binding.scheduleDetailTvSchedule.text = LocalDateTime.parse(exercise.scheduleDate).format(
            DateTimeFormatter.ofPattern("h:mm a")
        )
        binding.scheduleDetailTvDescription.text = exercise.description
        binding.scheduleDetailTvTotalSteps.text = "${exercise.steps.size} bước"

        val adapter = ExerciseStepListAdapter(exercise.steps)
        binding.scheduleDetailRcvList.adapter = adapter
        binding.scheduleDetailRcvList.layoutManager = LinearLayoutManager(this)

        binding.scheduleDetailFabReschedule.setOnClickListener {
            val intent = Intent(this, HomeRescheduleActivity::class.java)
            intent.putExtra("EXERCISE_THUMBNAIL", exercise.image)
            intent.putExtra("EXERCISE_TITLE", exercise.title)
            intent.putExtra(
                "EXERCISE_DETAIL", "${exercise.duration} Phút | ${exercise.calories} Calo"
            )

            closeOverlay()
            startActivity(intent)
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

    private fun openOverlay() {
        binding.scheduleDetailClView.setBackgroundResource(R.drawable.bg_overlay)
        binding.scheduleDetailFabStart.visibility = View.VISIBLE
        binding.scheduleDetailFabStartLabel.visibility = View.VISIBLE
        binding.scheduleDetailFabReschedule.visibility = View.VISIBLE
        binding.scheduleDetailFabRescheduleLabel.visibility = View.VISIBLE
        binding.scheduleDetailFabDelete.visibility = View.VISIBLE
        binding.scheduleDetailFabDeleteLabel.visibility = View.VISIBLE
        binding.scheduleDetailFabMenu.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.fab_close_color)
        binding.scheduleDetailFabMenu.setImageResource(R.drawable.ic_round_close_24)
        isOpenFloatingContainer = true
    }

    private fun closeOverlay() {
        binding.scheduleDetailClView.background = null
        binding.scheduleDetailFabStart.visibility = View.INVISIBLE
        binding.scheduleDetailFabStartLabel.visibility = View.INVISIBLE
        binding.scheduleDetailFabReschedule.visibility = View.INVISIBLE
        binding.scheduleDetailFabRescheduleLabel.visibility = View.INVISIBLE
        binding.scheduleDetailFabDelete.visibility = View.INVISIBLE
        binding.scheduleDetailFabDeleteLabel.visibility = View.INVISIBLE
        binding.scheduleDetailFabMenu.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.primary)
        binding.scheduleDetailFabMenu.setImageResource(R.drawable.ic_round_menu_24)
        isOpenFloatingContainer = false
    }
}
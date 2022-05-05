package com.h2a.fitbook.views.activities.schedule

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.home.ExerciseStepListAdapter
import com.h2a.fitbook.databinding.ActivityScheduleDetailBinding
import com.h2a.fitbook.shared.dialogs.ConfirmDialog
import com.h2a.fitbook.viewmodels.schedule.ScheduleDetailViewModel
import com.h2a.fitbook.views.activities.home.HomeRescheduleActivity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleDetailActivity : AppCompatActivity() {
    private var _binding: ActivityScheduleDetailBinding? = null
    private val binding get() = _binding!!

    private var isOpenFloatingContainer: Boolean = false

    private val viewModel: ScheduleDetailViewModel by viewModels()

    private val getSecondActivityIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data

                val duration = intent?.getLongExtra("RESCHEDULE_DURATION", 0)
                val set = intent?.getLongExtra("RESCHEDULE_SET", 0)
                val time = intent?.getStringExtra("RESCHEDULE_TIME")

                viewModel.measureDuration = duration!!.toLong()
                viewModel.scheduleDate = time.toString()
                viewModel.totalSet = set!!.toLong()
                viewModel.setSchedule(time.toString())
            }
        }

    @SuppressLint("SetTextI18n")
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
                    viewModel.deleteSchedule { itInner ->
                        if (itInner) {
                            Toast.makeText(
                                this, R.string.schedule_detail_fab_delete_ok, Toast.LENGTH_SHORT
                            ).show()
                            closeOverlay()
                            finish()
                        } else {
                            Toast.makeText(
                                this, R.string.schedule_detail_fab_delete_failed, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        binding.scheduleDetailFabStart.setOnClickListener {
            closeOverlay()
            val intent = Intent(this, ScheduleTimerActivity::class.java)
            intent.putExtra("SCHEDULE_TIMER_ID", viewModel.id)
            intent.putExtra("SCHEDULE_TIMER_SET", viewModel.totalSet)
            intent.putExtra("SCHEDULE_TIMER_DURATION", viewModel.measureDuration)
            intent.putExtra("SCHEDULE_TIMER_NAME", viewModel.title.value)
            startActivity(intent)
        }

        viewModel.thumbnail.observe(this) {
            if (it.isEmpty()) {
                binding.scheduleDetailImgThumbnail.setImageResource(R.drawable.bg_default_exercise)
            } else {
                binding.scheduleDetailImgThumbnail.load(viewModel.thumbnail.value) {
                    placeholder(R.drawable.bg_placeholder)
                    crossfade(true)
                }
            }
        }
        viewModel.title.observe(this) {
            binding.scheduleDetailTvTitle.text = it
        }
        viewModel.detail.observe(this) {
            binding.scheduleDetailTvDetail.text = it
        }
        viewModel.description.observe(this) {
            binding.scheduleDetailTvDescription.text = it
        }
        viewModel.steps.observe(this) {
            binding.scheduleDetailTvTotalSteps.text = "${it?.size} bước"

            val adapter = if (it == null) {
                ExerciseStepListAdapter(arrayListOf())
            } else {
                ExerciseStepListAdapter(it)
            }
            binding.scheduleDetailRcvList.adapter = adapter
            binding.scheduleDetailRcvList.layoutManager = object : LinearLayoutManager(this) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
        }
        viewModel.schedule.observe(this) {
            binding.scheduleDetailTvSchedule.text =
                LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME).format(
                    DateTimeFormatter.ofPattern("HH:mm")
                )
        }

        viewModel.measureDuration = intent.getLongExtra("SCHEDULE_DAILY_EXERCISE_DURATION", 0)
        viewModel.measureCalories = intent.getDoubleExtra("SCHEDULE_DAILY_EXERCISE_CALORIES", 0.0)
        viewModel.scheduleDate = intent.getStringExtra("SCHEDULE_DAILY_SCHEDULE_DATE").toString()
        viewModel.totalSet = intent.getLongExtra("SCHEDULE_DAILY_EXERCISE_TOTAL_SET", 0)
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        // get exercise by id from API
        val exerciseId = intent.getStringExtra("SCHEDULE_DAILY_EXERCISE_ID")
        viewModel.id = exerciseId.toString()
        binding.scheduleDetailClLoading.visibility = View.VISIBLE
        viewModel.getScheduleDetailById { isOk ->
            binding.scheduleDetailClLoading.visibility = View.GONE
            if (!isOk) {
                Toast.makeText(this, R.string.activity_home_detail_load_error, Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.scheduleDetailFabReschedule.setOnClickListener {
                    viewModel.getExerciseDetail(viewModel.id) { isOk, data ->
                        if (isOk) {
                            val intent = Intent(
                                this, HomeRescheduleActivity::class.java
                            )
                            intent.putExtra("EXERCISE_THUMBNAIL", data.image)
                            intent.putExtra("EXERCISE_ID", data.id)
                            intent.putExtra("EXERCISE_TITLE", data.name)
                            intent.putExtra("EXERCISE_DURATION", data.measureDuration)
                            intent.putExtra("EXERCISE_CALORIES", data.measureCalories.toString())
                            intent.putExtra("EXERCISE_MODE", "EDIT")
                            intent.putExtra("EXERCISE_DATE", viewModel.scheduleDate)
                            intent.putExtra("EXERCISE_SET", viewModel.totalSet)
                            intent.putExtra(
                                "EXERCISE_MEASURE_DURATION", viewModel.measureDuration
                            )

                            closeOverlay()
                            this.getSecondActivityIntent.launch(intent)
                        } else {
                            Toast.makeText(this, "Không thể lên lại lịch", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
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
        binding.scheduleDetailFabReschedule.visibility = View.VISIBLE
        binding.scheduleDetailFabRescheduleLabel.visibility = View.VISIBLE

        binding.scheduleDetailFabDelete.visibility = View.VISIBLE
        binding.scheduleDetailFabDeleteLabel.visibility = View.VISIBLE

        val today = LocalDate.now()
        val dayToCheck =
            LocalDateTime.parse(viewModel.scheduleDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        if (today.isAfter(LocalDate.parse(dayToCheck, DateTimeFormatter.ofPattern("dd/MM/yyyy")))) {
            binding.scheduleDetailFabStart.visibility = View.GONE
            binding.scheduleDetailFabStartLabel.visibility = View.GONE
        } else {
            binding.scheduleDetailFabStart.visibility = View.VISIBLE
            binding.scheduleDetailFabStartLabel.visibility = View.VISIBLE
        }

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
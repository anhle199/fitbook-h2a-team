package com.h2a.fitbook.views.activities.schedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityScheduleTimerBinding
import com.h2a.fitbook.shared.dialogs.ConfirmDialog
import com.h2a.fitbook.viewmodels.schedule.ScheduleTimerViewModel

class ScheduleTimerActivity : AppCompatActivity() {
    private var _binding: ActivityScheduleTimerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScheduleTimerViewModel by viewModels()

    private lateinit var countDownTimer: CountDownTimer

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

        binding.scheduleTimerTvTitle.text = intent.getStringExtra("SCHEDULE_TIMER_NAME")
        viewModel.id = intent.getStringExtra("SCHEDULE_TIMER_ID").toString()
        viewModel.totalSet = intent.getLongExtra("SCHEDULE_TIMER_SET", 0)
        viewModel.duration = intent.getLongExtra("SCHEDULE_TIMER_DURATION", 0)
        viewModel.timer = viewModel.duration // used to count

        binding.scheduleTimerTvDetail.text = "Tổng số set phải thực hiện là ${viewModel.totalSet}"

        updateTimeString()

        binding.scheduleTimerFabReplay.setOnClickListener {
            resetCountDown()
        }

        binding.scheduleTimerFabPlay.setOnClickListener {
            startCountDown()
        }

        binding.scheduleTimerFabPause.setOnClickListener {
            pauseCountDown()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {  // Back button action
                return if (viewModel.actualSeconds > 0) {
                    confirmOnDestroy {
                        if (it) {
                            finish()
                        }
                    }
                    true
                } else {
                    finish()
                    true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateTimeString() {
        binding.scheduleTimerTvTime.text = this.convertToTimeString(viewModel.timer)
    }

    private fun convertToTimeString(initSec: Long): String {
        val hours = initSec / 3600;
        val minutes = (initSec % 3600) / 60;
        val seconds = initSec % 60;

        val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return timeString
    }

    private fun startCountDown() {
        binding.scheduleTimerFabPlay.visibility = View.GONE
        binding.scheduleTimerFabPause.visibility = View.VISIBLE
        binding.scheduleTimerFabReplay.visibility = View.GONE

        countDownTimer = object : CountDownTimer(viewModel.timer * 1000, 1000) {
            override fun onFinish() {
                binding.scheduleTimerFabPause.visibility = View.GONE
                binding.scheduleTimerFabPlay.visibility = View.VISIBLE
                binding.scheduleTimerFabReplay.visibility = View.VISIBLE

                // update total set
                viewModel.actualSets += 1
                // show dialog and save
                alertResult()
            }

            override fun onTick(millisUntilFinished: Long) {
                viewModel.timer = millisUntilFinished / 1000
                viewModel.actualSeconds++
                updateTimeString()
            }

        }.start()
    }

    private fun resetCountDown() {
        countDownTimer.cancel()
        viewModel.timer = viewModel.duration
        updateTimeString()
    }

    private fun pauseCountDown() {
        binding.scheduleTimerFabPause.visibility = View.GONE
        binding.scheduleTimerFabPlay.visibility = View.VISIBLE
        binding.scheduleTimerFabReplay.visibility = View.VISIBLE
        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseCountDown()
    }

    override fun onDestroy() {
        super.onDestroy()
        pauseCountDown()
    }

    private fun confirmOnDestroy(callback: (Boolean) -> Unit) {
        pauseCountDown() // confirm and save
        val dialog = ConfirmDialog(
            "Cảnh báo", "Nếu tiếp tục, kết quả luyện tập sẽ không được lưu lại"
        )
        dialog.show(supportFragmentManager, "confirm")
        dialog.onButtonClick = {
            callback(it)
        }
    }

    private fun alertResult() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Thông báo").setMessage("Kết quả đang được lưu, vui lòng đợi")
        dialog.setCancelable(false)
        val alert = dialog.create()
        alert.show()

        viewModel.saveResult {
            alert.hide()
            if (it) {
                Toast.makeText(this, "Lưu kết quả thành công", Toast.LENGTH_SHORT).show()
                // reset actualWorkoutTime
                viewModel.actualSeconds = 0
            } else {
                Toast.makeText(this, "Lưu kết quả thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
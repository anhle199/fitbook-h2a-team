package com.h2a.fitbook.views.fragments.schedule

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.schedule.ScheduleListAdapter
import com.h2a.fitbook.databinding.FragmentScheduleBinding
import com.h2a.fitbook.models.ScheduleModel
import com.h2a.fitbook.viewmodels.schedule.ScheduleViewModel
import com.h2a.fitbook.views.activities.schedule.ScheduleDailyActivity

class ScheduleFragment : Fragment() {
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScheduleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.scheduleClLoading.visibility = View.VISIBLE
        viewModel.getExerciseList { isOk, data ->
            binding.scheduleClLoading.visibility = View.GONE
            handleLoadData(isOk, data)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleLoadData(isOk: Boolean, data: ArrayList<ScheduleModel>) {
        if (isOk) {
            if (data.size == 0) {
                binding.scheduleLnAlert.visibility = View.VISIBLE
            } else {
                binding.scheduleLnAlert.visibility = View.GONE
                val adapter = ScheduleListAdapter(data)
                adapter.onItemClick = {
                    val intent = Intent(this.context, ScheduleDailyActivity::class.java)
                    intent.putExtra("SCHEDULE_DAILY_DATE", it.date)
                    startActivity(intent)
                }
                binding.scheduleRcvDateList.adapter = adapter
                binding.scheduleRcvDateList.layoutManager = LinearLayoutManager(this.context)
            }
        } else {
            Toast.makeText(this.context, R.string.schedule_load_error, Toast.LENGTH_SHORT).show()
        }
    }
}
package com.h2a.fitbook.views.fragments.schedule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.schedule.ScheduleListAdapter
import com.h2a.fitbook.databinding.FragmentScheduleBinding
import com.h2a.fitbook.models.ScheduleModel
import com.h2a.fitbook.views.activities.schedule.ScheduleDailyActivity

class ScheduleFragment : Fragment() {
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val getSecondActivityIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val scheduleList = arrayListOf(
            ScheduleModel("04/04/2022", 9, 90),
            ScheduleModel("05/04/2022", 9, 90),
            ScheduleModel("06/04/2022", 9, 90),
            ScheduleModel("07/04/2022", 9, 90),
            ScheduleModel("08/04/2022", 9, 90),
            ScheduleModel("09/04/2022", 9, 90),
            ScheduleModel("10/04/2022", 9, 90)
        )
        val adapter = ScheduleListAdapter(scheduleList)
        adapter.onItemClick = {
            val intent = Intent(this.context, ScheduleDailyActivity::class.java)
            intent.putExtra("SCHEDULE_DAILY_DATE", it.date)
            this.getSecondActivityIntent.launch(intent)
        }
        binding.scheduleRcvDateList.adapter = adapter
        binding.scheduleRcvDateList.layoutManager = LinearLayoutManager(this.context)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
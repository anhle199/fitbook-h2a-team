package com.h2a.fitbook.views.fragments.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.home.HomeListAdapter
import com.h2a.fitbook.databinding.FragmentHomeBinding
import com.h2a.fitbook.models.ExerciseModel
import com.h2a.fitbook.views.activities.home.HomeDetailActivity

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
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
        _binding = FragmentHomeBinding.inflate(
            inflater, container, false
        )
        val checkScheduleBtn: Button = binding.homeBtnCheckSchedule
        checkScheduleBtn.setOnClickListener {
            val navController = findNavController(this)
            navController.navigate(R.id.nav_schedule)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val exerciseList = arrayListOf<ExerciseModel>()
        for (i in 1..7) {
            exerciseList.add(
                ExerciseModel(
                    i.toString(),
                    "Bài $i",
                    2,
                    50,
                    "https://i.ibb.co/qg4Pjx4/bg-home-list-item-running.png",
                    "",
                    arrayListOf()
                )
            )
        }

        val adapter = HomeListAdapter(exerciseList)
        binding.homeRcvExerciseList.adapter = adapter
        binding.homeRcvExerciseList.layoutManager = LinearLayoutManager(this.context)

        adapter.onDetailButtonClick = {
            val intent = Intent(this.context, HomeDetailActivity::class.java)
            intent.putExtra("EXERCISE_ID", "1")
            this.getSecondActivityIntent.launch(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.h2a.fitbook.views.fragments.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.home.HomeListAdapter
import com.h2a.fitbook.databinding.FragmentHomeBinding
import com.h2a.fitbook.models.ExerciseListItemModel
import com.h2a.fitbook.viewmodels.home.HomeViewModel
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

    private val viewModel: HomeViewModel by activityViewModels()

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
        binding.homeClLoading.visibility = View.VISIBLE
        viewModel.getExerciseList { isOk, data ->
            binding.homeClLoading.visibility = View.GONE
            handleLoadData(isOk, data)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleLoadData(isOk: Boolean, data: ArrayList<ExerciseListItemModel>) {
        if (isOk) {
            val adapter = HomeListAdapter(data)
            binding.homeRcvExerciseList.adapter = adapter
            binding.homeRcvExerciseList.layoutManager = LinearLayoutManager(this.context)

            adapter.onDetailButtonClick = {
                val intent = Intent(this.context, HomeDetailActivity::class.java)
                intent.putExtra("EXERCISE_ID", it.id)
                this.getSecondActivityIntent.launch(intent)
            }
        } else {
            Toast.makeText(this.context, R.string.fragment_home_load_error, Toast.LENGTH_SHORT)
                .show()
        }
    }
}

package com.h2a.fitbook.views.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.home.ExerciseStepListAdapter
import com.h2a.fitbook.databinding.FragmentExerciseDetailBinding
import com.h2a.fitbook.viewmodels.home.ExerciseDetailViewModel

class ExerciseDetailFragment : Fragment() {
    private var _binding: FragmentExerciseDetailBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val viewModel: ExerciseDetailViewModel by activityViewModels()

        _binding = FragmentExerciseDetailBinding.inflate(inflater, container, false)

        binding.exerciseDetailImgThumbnail.load(viewModel.thumbnail.value) {
            placeholder(R.drawable.bg_placeholder)
            crossfade(true)
        }
        binding.exerciseDetailTvTitle.text = viewModel.title.value.toString()
        binding.exerciseDetailTvDetail.text = viewModel.detail.value.toString()
        binding.exerciseDetailTvDescription.text = viewModel.description.value.toString()
        binding.exerciseDetailTvTotalSteps.text = "${viewModel.steps.value?.size} bước"

        val stepValue = viewModel.steps.value
        val adapter = if (stepValue == null) {
            ExerciseStepListAdapter(arrayListOf())
        } else {
            ExerciseStepListAdapter(stepValue)
        }

        binding.exerciseDetailRcvList.adapter = adapter
        binding.exerciseDetailRcvList.layoutManager = LinearLayoutManager(this.context)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
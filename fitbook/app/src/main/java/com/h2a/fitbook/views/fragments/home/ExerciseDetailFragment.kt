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

        viewModel.isLoading.observe(this.viewLifecycleOwner) {
            binding.exerciseDetailClLoading.visibility = if (it == true) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        viewModel.thumbnail.observe(this.viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.exerciseDetailImgThumbnail.setImageResource(R.drawable.bg_default_exercise)
            } else {
                binding.exerciseDetailImgThumbnail.load(viewModel.thumbnail.value) {
                    placeholder(R.drawable.bg_placeholder)
                    crossfade(true)
                }
            }
        }
        viewModel.title.observe(this.viewLifecycleOwner) {
            binding.exerciseDetailTvTitle.text = it
        }
        viewModel.detail.observe(this.viewLifecycleOwner) {
            binding.exerciseDetailTvDetail.text = it
        }
        viewModel.description.observe(this.viewLifecycleOwner) {
            binding.exerciseDetailTvDescription.text = it
        }
        viewModel.steps.observe(this.viewLifecycleOwner) {
            binding.exerciseDetailTvTotalSteps.text = "${it?.size} bước"

            val adapter = if (it == null) {
                ExerciseStepListAdapter(arrayListOf())
            } else {
                ExerciseStepListAdapter(it)
            }
            binding.exerciseDetailRcvList.adapter = adapter
            binding.exerciseDetailRcvList.layoutManager =
                object : LinearLayoutManager(this.context) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
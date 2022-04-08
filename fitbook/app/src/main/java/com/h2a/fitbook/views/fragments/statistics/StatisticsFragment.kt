package com.h2a.fitbook.views.fragments.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.statistics.StatisticsAdapter
import com.h2a.fitbook.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager2 = binding.statisticsViewPager2
        viewPager2.adapter = StatisticsAdapter(this)

        val tabTitles = resources.getStringArray(R.array.statistics_tab_titles)
        TabLayoutMediator(binding.statisticsTabLayout, viewPager2) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        val menuFab = binding.statisticsFabMenu
        menuFab.setOnClickListener {
            if (this.context != null) {
                val safeContext = this.requireContext()

                val visibility = if (binding.statisticsFabShare.isVisible) View.INVISIBLE else View.VISIBLE
                binding.statisticsFabShare.visibility = visibility
                binding.statisticsFabDownload.visibility = visibility

                if (visibility == View.VISIBLE) {
                    menuFab.backgroundTintList = ContextCompat.getColorStateList(safeContext, R.color.fab_close_color)
                    menuFab.setImageResource(R.drawable.ic_round_close_24)
                } else {
                    menuFab.backgroundTintList = ContextCompat.getColorStateList(safeContext, R.color.primary)
                    menuFab.setImageResource(R.drawable.ic_round_menu_24)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

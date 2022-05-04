package com.h2a.fitbook.views.fragments.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.h2a.fitbook.R
import com.h2a.fitbook.adapters.statistics.StatisticsAdapter
import com.h2a.fitbook.databinding.FragmentStatisticsBinding
import com.h2a.fitbook.viewmodels.statistics.StatisticInDateViewModel
import com.h2a.fitbook.viewmodels.statistics.StatisticInWeekViewModel
import com.h2a.fitbook.viewmodels.statistics.StatisticsViewModel

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
        val viewPager2 = binding.statisticsVp2Content
        viewPager2.adapter = StatisticsAdapter(this)

        val tabTitles = resources.getStringArray(R.array.statistics_tab_titles)
        val tabBar = binding.statisticsTlTabBar
        TabLayoutMediator(tabBar, viewPager2) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // Set up view model and its refreshing state observed
        val viewModel = ViewModelProvider(requireActivity())[StatisticsViewModel::class.java]
        viewModel.isRefreshing.observe(this.viewLifecycleOwner) { isRefreshing ->
            binding.statisticsSrlPullToRefresh.isRefreshing = isRefreshing

            // Enabled/Disable swiping or clicking to switch to another tab.
            viewPager2.isUserInputEnabled = !isRefreshing
            for (i in 0 until tabBar.tabCount) {
                tabBar.getTabAt(i)!!.view.isClickable = !isRefreshing
            }
        }

        addActionForRefreshingGesture()
    }

    private fun addActionForRefreshingGesture() {
        val viewPager2 = binding.statisticsVp2Content
        val tabBar = binding.statisticsTlTabBar
        val swipeLayout = binding.statisticsSrlPullToRefresh

        swipeLayout.setOnRefreshListener {
            // Disable swiping or clicking to switch to another tab.
            viewPager2.isUserInputEnabled = false
            for (i in 0 until tabBar.tabCount) {
                tabBar.getTabAt(i)!!.view.isClickable = false
            }

            when (tabBar.selectedTabPosition) {
                0 -> {
                    val childViewModel = ViewModelProvider(requireActivity())[StatisticInDateViewModel::class.java]
                    childViewModel.setFetchingState(true)
                }
                1 -> {
                    val childViewModel = ViewModelProvider(requireActivity())[StatisticInWeekViewModel::class.java]
                    childViewModel.setFetchingState(true)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

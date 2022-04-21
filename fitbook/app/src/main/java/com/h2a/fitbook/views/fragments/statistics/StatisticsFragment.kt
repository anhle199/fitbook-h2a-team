package com.h2a.fitbook.views.fragments.statistics

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        val viewPager2 = binding.statisticsVp2Content
        viewPager2.adapter = StatisticsAdapter(this)

        val tabTitles = resources.getStringArray(R.array.statistics_tab_titles)
        val tabBar = binding.statisticsTlTabBar
        TabLayoutMediator(tabBar, viewPager2) { tab, position ->
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

        val swipeLayout = binding.statisticsSrlPullToRefresh
        swipeLayout.setOnRefreshListener {
            Toast.makeText(context, "Tab is refreshing", Toast.LENGTH_LONG).show()

            // Disable swiping or clicking to switch to another tab.
            viewPager2.isUserInputEnabled = false
            for (i in 0 until tabBar.tabCount) {
                tabBar.getTabAt(i)!!.view.isClickable = false
            }

            Handler(Looper.getMainLooper())
                .postDelayed(
                    {
                        // Stop refreshing animation.
                        swipeLayout.isRefreshing = false

                        // Enable swiping or clicking to switch tab.
                        viewPager2.isUserInputEnabled = true
                        for (i in 0 until tabBar.tabCount) {
                            tabBar.getTabAt(i)!!.view.isClickable = true
                        }
                    },
                    3000
                )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.h2a.fitbook.adapters.statistics

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.h2a.fitbook.views.fragments.statistics.StatisticInDateFragment
import com.h2a.fitbook.views.fragments.statistics.StatisticInWeekFragment

class StatisticsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StatisticInDateFragment()
            1 -> StatisticInWeekFragment()
            else -> Fragment()
        }
    }

}

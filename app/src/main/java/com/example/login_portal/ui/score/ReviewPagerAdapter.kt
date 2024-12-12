package com.example.login_portal.ui.score

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.login_portal.ui.score.fragments.SemesterFragment
import com.example.login_portal.ui.score.fragments.SummaryFragment
import com.example.login_portal.ui.score.fragments.TotalFragment
import com.example.login_portal.ui.score.fragments.YearFragment

class ReviewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments = listOf(
        SummaryFragment(),
        TotalFragment(),
        SemesterFragment(),
        YearFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun getFragment(position: Int): Fragment? {
        return fragments.getOrNull(position)
    }
}


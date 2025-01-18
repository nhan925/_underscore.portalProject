package com.example.login_portal.ui.admin_manage_class


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ClassDetailsPagerAdapter(activity: FragmentActivity) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ClassInfoFragment()
            1 -> StudentsFragment()
            2 -> GradesFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}
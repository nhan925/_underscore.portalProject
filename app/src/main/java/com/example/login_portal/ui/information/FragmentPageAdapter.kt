package com.example.login_portal.ui.information

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentPageAdapter (
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Information_General() // Trả về fragment Information_General
            1 -> Information_Detail() // Trả về fragment Information_Detail
            2 -> Information_Contact() // Trả về fragment Information_Contact
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}
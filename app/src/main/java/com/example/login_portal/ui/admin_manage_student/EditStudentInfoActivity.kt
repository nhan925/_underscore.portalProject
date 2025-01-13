package com.example.login_portal.ui.admin_manage_student

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.login_portal.R
import com.example.login_portal.databinding.ActivityAdminUpdateStudentInfoBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.example.login_portal.BaseActivity

class EditStudentInfoActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminUpdateStudentInfoBinding
    private lateinit var viewModel: AdminManageStudentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUpdateStudentInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the student ID from the intent
        val studentId = intent.getStringExtra("STUDENT_ID")

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(AdminManageStudentViewModel::class.java)
        if (studentId != null) {
            viewModel.loadStudentInfoForEditing(studentId)
        }

        // Setup TabLayout and ViewPager2
        val adapter = FragmentPageAdapter(supportFragmentManager, lifecycle)
        binding.adminUpdateStudentViewpager.adapter = adapter

        TabLayoutMediator(binding.adminUpdateStudentTablayout, binding.adminUpdateStudentViewpager) { tab, position ->
            tab.text = when (position) {
                0 -> "General"
                1 -> "Detail"
                2 -> "Contact"
                else -> throw IllegalArgumentException("Invalid position")
            }
        }.attach()

        // Handle the back button
        binding.topBar.findViewById<View>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }
    }
}

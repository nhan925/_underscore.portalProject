package com.example.login_portal.ui.admin_manage_student

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.R
import com.example.login_portal.databinding.ActivityAdminUpdateStudentInfoBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.example.login_portal.BaseActivity
import com.bumptech.glide.Glide

class EditStudentInfoActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminUpdateStudentInfoBinding
    private lateinit var viewModel: AdminManageStudentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUpdateStudentInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val studentId = intent.getStringExtra("STUDENT_ID")
        viewModel = ViewModelProvider(this).get(AdminManageStudentViewModel::class.java)

        // Fetch and observe student list
        viewModel.fetchStudentList()
        viewModel.studentList.observe(this) { studentList ->
            if (!studentList.isNullOrEmpty() && studentId != null) {
                viewModel.loadStudentInfoForEditing(studentId)
            }
        }

        // Observe selected student
        viewModel.selectedStudent.observe(this) { student ->
            if (student != null) {
                Glide.with(this)
                    .load(student.avatarUrl ?: R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(binding.adminUpdateStudentAvatar)
            }
        }

        setupTabLayoutAndViewPager()
        setupBackButton()
    }

    private fun setupTabLayoutAndViewPager() {
        val adapter = FragmentPageAdapter(supportFragmentManager, lifecycle)
        binding.adminUpdateStudentViewpager.adapter = adapter

        TabLayoutMediator(binding.adminUpdateStudentTablayout, binding.adminUpdateStudentViewpager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.infor_student_frag_information_general)
                1 -> getString(R.string.infor_student_frag_information_detail)
                2 -> getString(R.string.infor_student_frag_information_contact)
                else -> throw IllegalArgumentException("Invalid position")
            }
        }.attach()
    }

    private fun setupBackButton() {
        binding.topBar.findViewById<View>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }
    }
}

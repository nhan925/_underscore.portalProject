package com.example.login_portal.ui.admin_manage_class

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.login_portal.BaseActivity
import com.example.login_portal.databinding.ActivityClassDetailsBinding
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import com.example.login_portal.R

class ClassDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityClassDetailsBinding
    private val viewModel: ClassDetailsViewModel by viewModels()
    private lateinit var pagerAdapter: ClassDetailsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val classId = intent.getStringExtra("class_id") ?: run {
            Toast.makeText(this, getString(R.string.error_invalid_class_id), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupViewPager()
        loadClassData(classId)
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.apply {
            btnBack.setOnClickListener { finish() }
        }
    }

    private fun setupViewPager() {
        pagerAdapter = ClassDetailsPagerAdapter(this)

        binding.apply {
            viewPager.apply {
                adapter = pagerAdapter
            }

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.tab_info)
                    1 -> getString(R.string.tab_students)
                    2 -> getString(R.string.tab_grades)
                    else -> ""
                }
            }.attach()
        }
    }

    private fun loadClassData(classId: String) {
        viewModel.loadClassInfo(classId)
    }

    private fun observeViewModel() {
        viewModel.classInfo.observe(this) { classInfo ->
            // Handle class info updates if needed
        }

        viewModel.error.observe(this) { error ->
            when (error) {
                is ClassDetailsViewModel.ErrorState.Error -> {
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                }
                else -> { /* Handle other states */ }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // Show/hide loading indicator if needed
        }
    }
}
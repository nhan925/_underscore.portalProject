package com.example.login_portal.ui.admin_manage_class

import com.example.login_portal.BaseActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.example.login_portal.R
import com.example.login_portal.databinding.ActivityCreateClassBinding
import com.example.login_portal.ui.admin_manage_class.CreateClassViewModel


class CreateClassActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateClassBinding
    private val viewModel: CreateClassViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupDropdowns()
        setupButtons()
    }

    private fun setupDropdowns() {
        // Registration Period Dropdown
        viewModel.registrationPeriods.observe(this) { periods ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                periods.map { it.name }
            )
            binding.registrationPeriodDropdown.setAdapter(adapter)
            binding.registrationPeriodDropdown.setOnItemClickListener { _, _, position, _ ->
                viewModel.setRegistrationPeriod(periods[position])
            }
        }

        // Course Dropdown
        viewModel.courses.observe(this) { courses ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                courses.map { it.courseName }
            )
            binding.courseDropdown.setAdapter(adapter)
            binding.courseDropdown.setOnItemClickListener { _, _, position, _ ->
                viewModel.setCourse(courses[position])
            }
        }

        // Lecturer Dropdown
        viewModel.lecturers.observe(this) { lecturers ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                lecturers.map { it.fullName }
            )
            binding.lecturerDropdown.setAdapter(adapter)
            binding.lecturerDropdown.setOnItemClickListener { _, _, position, _ ->
                viewModel.setLecturer(lecturers[position])
            }
        }

        // Day of Week Dropdown
        val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val dayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            daysOfWeek
        )
        binding.dayOfWeekDropdown.setAdapter(dayAdapter)
        binding.dayOfWeekDropdown.setOnItemClickListener { _, _, position, _ ->
            viewModel.setDayOfWeek(daysOfWeek[position])
        }
    }
    private fun showValidationErrors(errorKeys: String) {
        errorKeys.split("|")
            .map { key -> getString(resources.getIdentifier(key, "string", packageName)) }
            .joinToString("\n")
            .let { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
    }



    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCreate.setOnClickListener {
            viewModel.createClass(
                onSuccess = {
                    Toast.makeText(this, getString(R.string.msg_class_created), Toast.LENGTH_SHORT).show()
                    finish()
                },
                onError = { errorKeys ->
                    showValidationErrors(errorKeys)
                }
            )
        }
    }

    private fun validateInputs(): Boolean {
        // Add validation logic here
        return true
    }
}
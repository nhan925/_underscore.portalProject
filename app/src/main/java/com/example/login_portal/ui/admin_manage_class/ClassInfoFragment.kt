package com.example.login_portal.ui.admin_manage_class

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.login_portal.databinding.FragmentClassInfoBinding
import com.google.android.material.textfield.TextInputEditText

class ClassInfoFragment : Fragment() {
    private var _binding: FragmentClassInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClassDetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassInfoBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropdowns()
        setupButtons()
        observeClassInfo()
    }

    private fun setupDropdowns() {
        // Registration Period Dropdown
        viewModel.registrationPeriods.observe(viewLifecycleOwner) { periods ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                periods.map { it.name }
            )
            binding.registrationPeriodDropdown.apply {
                setAdapter(adapter)
                // Adjust dropdown height and offset to ensure it opens downward
                dropDownVerticalOffset = 8
                setDropDownHeight(resources.displayMetrics.heightPixels / 3)

                viewModel.classInfo.value?.let { classInfo ->
                    val currentPeriod = periods.find { it.registrationPeriodId == classInfo.registrationPeriodId }
                    currentPeriod?.let {
                        setText(it.name, false)
                    }
                }

                setOnItemClickListener { _, _, position, _ ->
                    viewModel.setRegistrationPeriod(periods[position])
                }
            }
        }

        // Course Dropdown
        viewModel.courses.observe(viewLifecycleOwner) { courses ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                courses.map { it.courseName }
            )
            binding.courseDropdown.apply {
                setAdapter(adapter)
                dropDownVerticalOffset = 8
                setDropDownHeight(resources.displayMetrics.heightPixels / 3)

                viewModel.classInfo.value?.let { classInfo ->
                    val currentCourse = courses.find { it.courseId == classInfo.courseId }
                    currentCourse?.let {
                        setText(it.courseName, false)
                    }
                }

                setOnItemClickListener { _, _, position, _ ->
                    viewModel.setCourse(courses[position])
                }
            }
        }

        // Lecturer Dropdown
        viewModel.lecturers.observe(viewLifecycleOwner) { lecturers ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                lecturers.map { it.fullName }
            )
            binding.lecturerDropdown.apply {
                setAdapter(adapter)
                dropDownVerticalOffset = 8
                setDropDownHeight(resources.displayMetrics.heightPixels / 3)

                viewModel.classInfo.value?.let { classInfo ->
                    val currentLecturer = lecturers.find { it.lecturerId == classInfo.lecturerId }
                    currentLecturer?.let {
                        setText(it.fullName, false)
                    }
                }

                setOnItemClickListener { _, _, position, _ ->
                    viewModel.setLecturer(lecturers[position])
                }
            }
        }

        // Day of Week Dropdown
        val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val dayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            daysOfWeek
        )
        binding.dayOfWeekDropdown.apply {
            setAdapter(dayAdapter)
            dropDownVerticalOffset = 8
            setDropDownHeight(resources.displayMetrics.heightPixels / 3)

            viewModel.classInfo.value?.let { classInfo ->
                setText(classInfo.dayOfWeek, false)
            }

            setOnItemClickListener { _, _, position, _ ->
                viewModel.setDayOfWeek(daysOfWeek[position])
            }
        }
    }

    private fun observeClassInfo() {
        viewModel.classInfo.observe(viewLifecycleOwner) { classInfo ->
            classInfo?.let { info ->
                binding.classNameInput.setText(info.className)
                binding.startPeriodInput.setText(info.startPeriod.toString())
                binding.endPeriodInput.setText(info.endPeriod.toString())
                binding.roomInput.setText(info.room)
                binding.maxEnrollmentInput.setText(info.maxEnrollment.toString())

                binding.registrationPeriodDropdown.setText(
                    viewModel.registrationPeriods.value?.find {
                        it.registrationPeriodId == info.registrationPeriodId
                    }?.name ?: "",
                    false
                )
                binding.courseDropdown.setText(
                    viewModel.courses.value?.find {
                        it.courseId == info.courseId
                    }?.courseName ?: "",
                    false
                )
                binding.lecturerDropdown.setText(
                    viewModel.lecturers.value?.find {
                        it.lecturerId == info.lecturerId
                    }?.fullName ?: "",
                    false
                )
                binding.dayOfWeekDropdown.setText(info.dayOfWeek, false)

                // Ensure default selections are set
                viewModel.setDayOfWeek(info.dayOfWeek)
                viewModel.registrationPeriods.value?.find {
                    it.registrationPeriodId == info.registrationPeriodId
                }?.let {
                    viewModel.setRegistrationPeriod(
                        it
                    )
                }
                viewModel.courses.value?.find { it.courseId == info.courseId }?.let {
                    viewModel.setCourse(
                        it
                    )
                }
                viewModel.lecturers.value?.find { it.lecturerId == info.lecturerId }?.let {
                    viewModel.setLecturer(
                        it
                    )
                }
            }
        }
    }



    private fun setupButtons() {
        binding.updateButton.setOnClickListener {
            if (validateInputs()) {
                updateClassInfo()
            }
        }

        binding.deleteButton.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun validateInputs(): Boolean {
        val startPeriod = binding.startPeriodInput.text.toString().toIntOrNull()
        val endPeriod = binding.endPeriodInput.text.toString().toIntOrNull()
        val maxEnrollment = binding.maxEnrollmentInput.text.toString().toIntOrNull()

        when {
            binding.classNameInput.text.isNullOrBlank() -> {
                showError("Class name is required")
                return false
            }
            startPeriod == null || startPeriod !in 1..10 -> {
                showError("Start period must be between 1 and 10")
                return false
            }
            endPeriod == null || endPeriod !in 1..10 -> {
                showError("End period must be between 1 and 10")
                return false
            }
            startPeriod > endPeriod -> {
                showError("Start period cannot be greater than end period")
                return false
            }
            binding.roomInput.text.isNullOrBlank() -> {
                showError("Room is required")
                return false
            }
            maxEnrollment == null || maxEnrollment <= 0 -> {
                showError("Maximum enrollment must be greater than 0")
                return false
            }
            binding.registrationPeriodDropdown.text.isNullOrBlank() -> {
                showError("Registration period is required")
                return false
            }
            binding.courseDropdown.text.isNullOrBlank() -> {
                showError("Course is required")
                return false
            }
            binding.lecturerDropdown.text.isNullOrBlank() -> {
                showError("Lecturer is required")
                return false
            }
            binding.dayOfWeekDropdown.text.isNullOrBlank() -> {
                showError("Day of week is required")
                return false
            }
        }

        return true
    }

    private fun updateClassInfo() {
        viewModel.updateClass(
            onSuccess = {
                showSuccess("Class updated successfully")
                activity?.finish()
            },
            onError = { error ->
                showError(error)
            }
        )
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Class")
            .setMessage("Are you sure you want to delete this class?")
            .setPositiveButton("Delete") { _, _ ->
                deleteClass()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteClass() {
        viewModel.deleteClass(
            onSuccess = {
                showSuccess("Class deleted successfully")
                activity?.finish()
            },
            onError = { error ->
                showError(error)
            }
        )
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

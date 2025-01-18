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
import com.example.login_portal.R
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
        val daysOfWeek = listOf("Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7")
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
                showError(getString(R.string.error_class_name_required))
                return false
            }
            startPeriod == null || startPeriod !in 1..10 -> {
                showError(getString(R.string.error_start_period_invalid))
                return false
            }
            endPeriod == null || endPeriod !in 1..10 -> {
                showError(getString(R.string.error_end_period_invalid))
                return false
            }
            startPeriod > endPeriod -> {
                showError(getString(R.string.error_start_period_greater))
                return false
            }
            binding.roomInput.text.isNullOrBlank() -> {
                showError(getString(R.string.error_room_required))
                return false
            }
            maxEnrollment == null || maxEnrollment <= 0 -> {
                showError(getString(R.string.error_max_enrollment))
                return false
            }
            binding.registrationPeriodDropdown.text.isNullOrBlank() -> {
                showError(getString(R.string.error_registration_period_required))
                return false
            }
            binding.courseDropdown.text.isNullOrBlank() -> {
                showError(getString(R.string.error_course_required))
                return false
            }
            binding.lecturerDropdown.text.isNullOrBlank() -> {
                showError(getString(R.string.error_lecturer_required))
                return false
            }
            binding.dayOfWeekDropdown.text.isNullOrBlank() -> {
                showError(getString(R.string.error_day_of_week_required))
                return false
            }
        }

        return true
    }

    private fun updateClassInfo() {
        viewModel.updateClass(
            onSuccess = {
                showSuccess(getString(R.string.msg_class_updated))
                activity?.finish()
            },
            onError = { error ->
                showError(error)
            }
        )
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_class))
            .setMessage(getString(R.string.confirm_delete_class))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteClass()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun deleteClass() {
        viewModel.deleteClass(
            onSuccess = {
                showSuccess(getString(R.string.msg_class_deleted))
                activity?.finish()
            },
            onError = { error ->
                showError(getString(R.string.class_deleted_failed))
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
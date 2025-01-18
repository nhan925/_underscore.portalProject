package com.example.login_portal.ui.admin_manage_course
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.login_portal.R
import com.example.login_portal.databinding.ActivityCourseDetailBinding
import com.example.login_portal.BaseActivity
import com.example.login_portal.ui.admin_manage_course.Course



class CourseDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityCourseDetailBinding
    private val viewModel: CourseViewModel by viewModels()
    private var isEditMode = false
    private var courseId: String = ""
    private var selectedMajorId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEditMode = intent.getBooleanExtra("IS_EDIT_MODE", false)
        courseId = intent.getStringExtra("COURSE_ID") ?: ""

        setupUI()
        setupObservers()
        setupListeners()
        setupMajorDropdown()
    }

    private fun setupUI() {
        binding.apply {
            // Configure button visibility
            layoutUpdateButtons.visibility = if (isEditMode) View.VISIBLE else View.GONE
            btnCreate.visibility = if (isEditMode) View.GONE else View.VISIBLE

            // Disable course ID editing in edit mode
            etCourseId.isEnabled = !isEditMode

            if (isEditMode && courseId.isNotEmpty()) {
                loadCourseData()
            }
        }
    }

    private fun setupMajorDropdown() {
        viewModel.majors.observe(this) { majors ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                majors.map { it.name }
            )

            binding.dropdownMajor.apply {
                setAdapter(adapter)

                // Pre-select major if in edit mode
                if (isEditMode) {
                    viewModel.courses.value?.find { it.id == courseId }?.let { course ->
                        val selectedMajor = majors.find { it.id == course.majorId }
                        selectedMajor?.let { major ->
                            setText(major.name, false)
                            selectedMajorId = major.id
                        }
                    }
                }

                setOnItemClickListener { _, _, position, _ ->
                    selectedMajorId = majors[position].id
                }

                // Set hint
                if (!isEditMode && text.isEmpty()) {
                    hint = context.getString(R.string.select_major)
                }
            }
        }
        viewModel.fetchMajors()
    }

    private fun loadCourseData() {
        viewModel.courses.observe(this) { courses ->
            val course = courses.find { it.id == courseId }
            course?.let { populateFields(it) }
        }
        viewModel.fetchCourses()
    }

    private fun populateFields(course: Course) {
        binding.apply {
            etCourseId.setText(course.id)
            etCourseName.setText(course.name)
            etCredits.setText(course.credits.toString())
            etTuitionFee.setText(course.tuitionFee.toString())
            etOutlineUrl.setText(course.outlineUrl)

            // Set major in dropdown (will be handled in setupMajorDropdown)
            viewModel.majors.value?.find { it.id == course.majorId }?.let { major ->
                dropdownMajor.setText(major.name, false)
                selectedMajorId = major.id
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnBack.setOnClickListener { finish() }

            btnCreate.setOnClickListener {
                if (validateInputs()) {
                    createCourse()
                }
            }

            btnUpdate.setOnClickListener {
                if (validateInputs()) {
                    updateCourse()
                }
            }

            btnDelete.setOnClickListener {
                showDeleteConfirmationDialog()
            }

            // Handle dropdown focus
            dropdownMajor.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus && dropdownMajor.text.isEmpty()) {
                    dropdownMajor.hint = getString(R.string.select_major)
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        binding.apply {
            if (etCourseId.text.isNullOrEmpty() ||
                etCourseName.text.isNullOrEmpty() ||
                etCredits.text.isNullOrEmpty() ||
                etTuitionFee.text.isNullOrEmpty() ||
                etOutlineUrl.text.isNullOrEmpty()
            ) {
                Toast.makeText(
                    this@CourseDetailActivity,
                    getString(R.string.fill_all_fields),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return true
    }

    private fun createCourse() {
        val request = ManageCourseRequest(
            operation = "create",
            courseId = binding.etCourseId.text.toString().trim(),
            courseName = binding.etCourseName.text.toString().trim(),
            credits = binding.etCredits.text.toString().toIntOrNull() ?: 0,
            tuitionFee = binding.etTuitionFee.text.toString().toIntOrNull() ?: 0,
            outlineUrl = binding.etOutlineUrl.text.toString().trim(),
            majorId = selectedMajorId
        )

        viewModel.manageCourse(request) { success ->
            if (success) {
                Toast.makeText(this, getString(R.string.course_created), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateCourse() {
        val request = ManageCourseRequest(
            operation = "update",
            courseId = courseId,
            courseName = binding.etCourseName.text.toString().trim(),
            credits = binding.etCredits.text.toString().toIntOrNull() ?: 0,
            tuitionFee = binding.etTuitionFee.text.toString().toIntOrNull() ?: 0,
            outlineUrl = binding.etOutlineUrl.text.toString().trim(),
            majorId = selectedMajorId
        )

        viewModel.manageCourse(request) { success ->
            if (success) {
                Toast.makeText(this, getString(R.string.course_updated), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_course))
            .setMessage(getString(R.string.delete_course_confirmation))
            .setPositiveButton(getString(R.string.delete_semester)) { _, _ ->
                deleteCourse()
            }
            .setNegativeButton(getString(R.string.request_detail_cancel_op), null)
            .show()
    }

    private fun deleteCourse() {
        val request = ManageCourseRequest(
            operation = "delete",
            courseId = courseId
        )

        viewModel.manageCourse(request) { success ->
            if (success) {
                Toast.makeText(this, getString(R.string.course_deleted), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.apply {
                // Toggle loading visibility if needed
                cardInfo.isEnabled = !isLoading
                btnCreate.isEnabled = !isLoading
                btnUpdate.isEnabled = !isLoading
                btnDelete.isEnabled = !isLoading
            }
        }

        viewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
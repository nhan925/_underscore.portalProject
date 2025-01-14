package com.example.login_portal.ui.admin_manage_semester

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.example.login_portal.BaseActivity
import com.example.login_portal.databinding.ActivityAdminSemesterBinding
import java.util.Calendar


class SemesterDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityAdminSemesterBinding
    private val viewModel: AdminManageSemesterViewModel by viewModels()
    private var isEditMode = false
    private var semesterId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSemesterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEditMode = intent.getBooleanExtra("IS_EDIT_MODE", false)
        semesterId = intent.getIntExtra("SEMESTER_ID", -1)

        setupUI()
        setupListeners()
        setupDatePickers()
    }

    private fun setupUI() {
        binding.apply {
            layoutUpdateButtons.visibility = if (isEditMode) View.VISIBLE else View.GONE
            btnCreate.visibility = if (isEditMode) View.GONE else View.VISIBLE

            if (isEditMode && semesterId != -1) {
                loadSemesterData()
            }
        }
    }

    private fun loadSemesterData() {
        viewModel.semesters.observe(this) { semesters ->
            val semester = semesters?.find { it.id == semesterId }
            semester?.let { populateFields(it) }
        }
        viewModel.fetchSemesters()
    }

    private fun populateFields(semester: Semester) {
        binding.apply {
            etSemesterNum.setText(semester.semesterNum.toString())
            etYear.setText(semester.year)
            etStartDate.setText(semester.startDate)
            etEndDate.setText(semester.endDate)
            etPaymentStartDate.setText(semester.paymentStartDate)
            etPaymentEndDate.setText(semester.paymentEndDate)
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }

            btnCreate.setOnClickListener {
                if (validateInputs()) {
                    createSemester()
                }
            }

            btnUpdate.setOnClickListener {
                if (validateInputs()) {
                    updateSemester()
                }
            }

            btnDelete.setOnClickListener {
                showDeleteConfirmationDialog()
            }
        }
    }

    private fun setupDatePickers() {
        val dateFields = listOf(
            binding.etStartDate,
            binding.etEndDate,
            binding.etPaymentStartDate,
            binding.etPaymentEndDate
        )

        dateFields.forEach { field ->
            field.setOnClickListener {
                showDatePicker(field)
            }
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val selectedDate = String.format("%d-%02d-%02d", year, month + 1, day)
                editText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun validateInputs(): Boolean {
        binding.apply {
            if (etSemesterNum.text.isNullOrEmpty() ||
                etYear.text.isNullOrEmpty() ||
                etStartDate.text.isNullOrEmpty() ||
                etEndDate.text.isNullOrEmpty() ||
                etPaymentStartDate.text.isNullOrEmpty() ||
                etPaymentEndDate.text.isNullOrEmpty()
            ) {
                Toast.makeText(this@SemesterDetailActivity,
                    "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun createSemester() {
        val request = CreateSemesterRequest(
            semesterNum = binding.etSemesterNum.text.toString().toInt(),
            year = binding.etYear.text.toString(),
            startDate = binding.etStartDate.text.toString(),
            endDate = binding.etEndDate.text.toString(),
            paymentStartDate = binding.etPaymentStartDate.text.toString(),
            paymentEndDate = binding.etPaymentEndDate.text.toString()
        )

        viewModel.createSemester(request) { success ->
            val message = if (success) "Semester created successfully"
            else "Failed to create semester"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            if (success) {
                finish()
            }
        }
    }

    private fun updateSemester() {
        val request = ManageSemesterRequest(
            operation = "update",
            semesterId = semesterId,
            semesterNum = binding.etSemesterNum.text.toString().toInt(),
            year = binding.etYear.text.toString(),
            startDate = binding.etStartDate.text.toString(),
            endDate = binding.etEndDate.text.toString(),
            paymentStartDate = binding.etPaymentStartDate.text.toString(),
            paymentEndDate = binding.etPaymentEndDate.text.toString()
        )

        viewModel.updateSemester(request) { success ->
            val message = if (success) "Semester updated successfully"
            else "Failed to update semester"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            if (success) {
                finish()
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Semester")
            .setMessage("Are you sure you want to delete this semester?")
            .setPositiveButton("Delete") { _, _ ->
                deleteSemester()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSemester() {
        viewModel.deleteSemester(semesterId) { success ->
            val message = if (success) "Semester deleted successfully"
            else "Failed to delete semester"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            if (success) {
                finish()
            }
        }
    }
}
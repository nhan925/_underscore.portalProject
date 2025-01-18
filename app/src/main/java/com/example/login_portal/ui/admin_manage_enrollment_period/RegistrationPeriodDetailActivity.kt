package com.example.login_portal.ui.admin_manage_enrollment_period

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.login_portal.R
import com.example.login_portal.databinding.ActivityRegistrationPeriodDetailBinding
import com.example.login_portal.BaseActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RegistrationPeriodDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityRegistrationPeriodDetailBinding
    private val viewModel: RegistrationPeriodViewModel by viewModels()
    private var isEditMode = false
    private var periodId: Int = 0
    private var selectedSemesterId: Int? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationPeriodDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEditMode = intent.getBooleanExtra("IS_EDIT_MODE", false)
        periodId = intent.getIntExtra("PERIOD_ID", 0)

        setupUI()
        setupObservers()
        setupListeners()
        setupSemesterDropdown()
        setupDatePickers()
    }

    private fun setupUI() {
        binding.apply {
            layoutUpdateButtons.visibility = if (isEditMode) View.VISIBLE else View.GONE
            btnCreate.visibility = if (isEditMode) View.GONE else View.VISIBLE

            // Disable semester selection and handle other edit mode configurations
            dropdownSemester.isEnabled = !isEditMode

            // Load data if in edit mode
            if (isEditMode && periodId != 0) {
                loadPeriodData()
            }

            // Configure text field hints
            tilPeriodName.hint = getString(R.string.period_name)
            tilStartDate.hint = getString(R.string.start_date)
            tilEndDate.hint = getString(R.string.end_date)
            tilMaxCredits.hint = getString(R.string.max_credits)
        }
    }

    private fun loadPeriodData() {
        viewModel.registrationPeriods.observe(this) { periods ->
            val period = periods.find { it.id == periodId }
            period?.let {
                populateFields(it)
            }
        }
        viewModel.fetchRegistrationPeriods()
    }


    private fun setupSemesterDropdown() {
        viewModel.semesters.observe(this) { semesters ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                semesters.map { "${it.year} - Semester ${it.semesterNum}" }
            )

            binding.dropdownSemester.apply {
                setAdapter(adapter)

                if (isEditMode) {
                    viewModel.registrationPeriods.value?.find { it.id == periodId }?.let { period ->
                        val selectedSemester = semesters.find { it.id == period.semesterId }
                        selectedSemester?.let { semester ->
                            setText("${semester.year} - Semester ${semester.semesterNum}", false)
                            selectedSemesterId = semester.id
                        }
                    }
                }

                setOnItemClickListener { _, _, position, _ ->
                    selectedSemesterId = semesters[position].id
                }

                if (!isEditMode && text.isEmpty()) {
                    hint = context.getString(R.string.select_semester)
                }
            }
        }
        viewModel.fetchSemesters()
    }

    private fun setupDatePickers() {
        binding.apply {
            etStartDate.setOnClickListener { showDatePicker(it as com.google.android.material.textfield.TextInputEditText) }
            etEndDate.setOnClickListener { showDatePicker(it as com.google.android.material.textfield.TextInputEditText) }
        }
    }

    private fun showDatePicker(editText: com.google.android.material.textfield.TextInputEditText) {
        val calendar = Calendar.getInstance()
        val currentDate = editText.text?.toString()
        if (!currentDate.isNullOrEmpty()) {
            calendar.time = dateFormat.parse(currentDate) ?: Date()
        }

        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                editText.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }



    private fun populateFields(period: RegistrationPeriod) {
        binding.apply {
            etPeriodName.setText(period.name)
            etStartDate.setText(period.startDate)
            etEndDate.setText(period.endDate)
            etMaxCredits.setText(period.maxCreditCanRegister.toString())
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnBack.setOnClickListener { finish() }

            btnCreate.setOnClickListener {
                if (validateInputs()) {
                    createPeriod()
                }
            }

            btnUpdate.setOnClickListener {
                if (validateInputs()) {
                    updatePeriod()
                }
            }

            btnDelete.setOnClickListener {
                showDeleteConfirmationDialog()
            }
        }
    }

    private fun validateInputs(): Boolean {
        binding.apply {
            if (etPeriodName.text.isNullOrEmpty() ||
                etStartDate.text.isNullOrEmpty() ||
                etEndDate.text.isNullOrEmpty() ||
                etMaxCredits.text.isNullOrEmpty() ||
                selectedSemesterId == null
            ) {
                Toast.makeText(
                    this@RegistrationPeriodDetailActivity,
                    getString(R.string.fill_all_fields),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }

            // Validate dates
            val startDate = dateFormat.parse(etStartDate.text.toString())
            val endDate = dateFormat.parse(etEndDate.text.toString())
            if (startDate != null && endDate != null && startDate.after(endDate)) {
                Toast.makeText(
                    this@RegistrationPeriodDetailActivity,
                    getString(R.string.invalid_date_range),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return true
    }

    private fun createPeriod() {
        val request = ManageRegistrationPeriodRequest(
            operation = "create",
            name = binding.etPeriodName.text.toString().trim(),
            semesterId = selectedSemesterId,
            startDate = binding.etStartDate.text.toString(),
            endDate = binding.etEndDate.text.toString(),
            maxCreditCanRegister = binding.etMaxCredits.text.toString().toIntOrNull()
        )

        viewModel.manageRegistrationPeriod(request) { success ->
            if (success) {
                Toast.makeText(this, getString(R.string.period_created), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updatePeriod() {
        val request = ManageRegistrationPeriodRequest(
            operation = "update",
            registrationPeriodId = periodId,
            name = binding.etPeriodName.text.toString().trim(),
            semesterId = selectedSemesterId,
            startDate = binding.etStartDate.text.toString(),
            endDate = binding.etEndDate.text.toString(),
            maxCreditCanRegister = binding.etMaxCredits.text.toString().toIntOrNull()
        )

        viewModel.manageRegistrationPeriod(request) { success ->
            if (success) {
                Toast.makeText(this, getString(R.string.period_updated), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_period))
            .setMessage(getString(R.string.delete_period_confirmation))
            .setPositiveButton(getString(R.string.delete_semester)) { _, _ ->
                deletePeriod()
            }
            .setNegativeButton(getString(R.string.request_detail_cancel_op), null)
            .show()
    }

    private fun deletePeriod() {
        val request = ManageRegistrationPeriodRequest(
            operation = "delete",
            registrationPeriodId = periodId
        )

        viewModel.manageRegistrationPeriod(request) { success ->
            if (success) {
                Toast.makeText(this, getString(R.string.period_deleted), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.apply {
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
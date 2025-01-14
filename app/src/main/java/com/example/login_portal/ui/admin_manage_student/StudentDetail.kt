package com.example.login_portal.ui.admin_manage_student

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.login_portal.databinding.FragmentAdminUpdateDetailInfoBinding
import java.util.*

class StudentDetail : Fragment() {

    private var _binding: FragmentAdminUpdateDetailInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminManageStudentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUpdateDetailInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupViewSwitchers()
        setupDatePicker()
        return binding.root
    }

    private fun setupViewSwitchers() {
        binding.infoDetailFragSwitchToEditMode.setOnClickListener {
            val isEditing = viewModel.isEditing.value ?: false
            toggleEditMode(!isEditing) // Toggle edit mode
        }

        binding.infoDetailFragAcceptChanges.setOnClickListener {
            viewModel.acceptChanges() // Save changes to backend
            toggleEditMode(false)     // Exit edit mode
        }

        binding.infoDetailFragCancelChanges.setOnClickListener {
            viewModel.stopEditing()   // Reset changes
            toggleEditMode(false)     // Exit edit mode
        }
    }

    private fun toggleEditMode(isEditing: Boolean) {
        binding.infoDetailFragViewSwitcherDob.showNext()
        binding.infoDetailFragViewSwitcherNationality.showNext()
        binding.infoDetailFragViewSwitcherEthnicity.showNext()
        binding.infoDetailFragViewSwitcherIdCard.showNext()
        binding.infoDetailFragViewSwitcherEmail.showNext()

        // Synchronize ViewModel editing state
        if (isEditing) {
            viewModel.startEditing()
        } else {
            viewModel.stopEditing()
        }
    }

    private fun setupDatePicker() {
        binding.infoDetailFragEtDob.setOnClickListener {
            if (viewModel.isEditing.value == true) {
                showDatePickerDialog()
            }
        }

        viewModel.showDatePickerEvent.observe(viewLifecycleOwner) { shouldShow ->
            if (shouldShow) {
                showDatePickerDialog()
                viewModel.onDatePickerShown()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        // Parse existing date if available
        viewModel.editDateOfBirth.value?.let { currentDate ->
            try {
                val parts = currentDate.split("/")
                if (parts.size == 3) {
                    calendar.set(Calendar.DAY_OF_MONTH, parts[0].toInt())
                    calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
                    calendar.set(Calendar.YEAR, parts[2].toInt())
                }
            } catch (e: Exception) {
                // If parsing fails, use current date
            }
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                viewModel.editDateOfBirth.value = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
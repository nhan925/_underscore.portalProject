package com.example.login_portal.ui.admin_manage_student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.login_portal.databinding.FragmentAdminUpdateGeneralInfoBinding

class StudentGeneral : Fragment() {

    private var _binding: FragmentAdminUpdateGeneralInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminManageStudentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUpdateGeneralInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupViewSwitchers()
        return binding.root
    }

    private fun setupViewSwitchers() {
        binding.infoGeneralFragSwitchToEditMode.setOnClickListener {
            val isEditing = viewModel.isEditing.value ?: false
            toggleEditMode(!isEditing) // Toggle edit mode
        }

        binding.infoGeneralFragAcceptChanges.setOnClickListener {
            viewModel.acceptChanges() // Save changes to backend
            toggleEditMode(false)     // Exit edit mode
        }

        binding.infoGeneralFragCancelChanges.setOnClickListener {
            viewModel.stopEditing()   // Reset changes
            toggleEditMode(false)     // Exit edit mode
        }
    }

    private fun toggleEditMode(isEditing: Boolean) {
        binding.infoGeneralFragViewSwitcherFullName.showNext()
        binding.infoGeneralFragViewSwitcherStudentId.showNext()
        binding.infoGeneralFragViewSwitcherMajor.showNext()
        binding.infoGeneralFragViewSwitcherAcademicProgram.showNext()
        binding.infoGeneralFragViewSwitcherGender.showNext()
        binding.infoGeneralFragViewSwitcherYearOfAdmission.showNext()

        if (isEditing) {
            viewModel.startEditing()
        } else {
            viewModel.stopEditing()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

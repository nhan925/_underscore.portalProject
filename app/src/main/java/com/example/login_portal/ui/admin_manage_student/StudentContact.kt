package com.example.login_portal.ui.admin_manage_student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.login_portal.databinding.FragmentAdminUpdateContactInfoBinding

class StudentContact : Fragment() {

    private var _binding: FragmentAdminUpdateContactInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminManageStudentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUpdateContactInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupViewSwitchers()
        return binding.root
    }

    private fun setupViewSwitchers() {
        binding.infoContactFragSwitchToEditMode.setOnClickListener {
            val isEditing = viewModel.isEditing.value ?: false
            toggleEditMode(!isEditing) // Toggle edit mode
        }

        binding.infoContactFragAcceptChanges.setOnClickListener {
            viewModel.acceptChanges() // Save changes to backend
            toggleEditMode(false)     // Exit edit mode
        }

        binding.infoContactFragCancelChanges.setOnClickListener {
            viewModel.stopEditing()   // Reset changes
            toggleEditMode(false)     // Exit edit mode
        }
    }

    private fun toggleEditMode(isEditing: Boolean) {
        binding.infoContactFragViewSwitcherEmail.showNext()
        binding.infoContactFragViewSwitcherPhone.showNext()
        binding.infoContactFragViewSwitcherAddress.showNext()

        // Synchronize ViewModel editing state
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

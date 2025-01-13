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

        // Observe and update UI
        viewModel.selectedStudent.observe(viewLifecycleOwner) { student ->
            binding.infoContactFragTvPersonalEmail.text = student?.personalEmail
            binding.infoContactFragTvPhoneLabel.text = student?.phoneNumber
            binding.infoContactFragTvAddress.text = student?.address
        }

        setupViewSwitchers()
        return binding.root
    }

    private fun setupViewSwitchers() {
        binding.infoContactFragSwitchToEditMode.setOnClickListener { viewModel.startEditing() }
        binding.infoContactFragAcceptChanges.setOnClickListener { viewModel.acceptChanges() }
        binding.infoContactFragCancelChanges.setOnClickListener { viewModel.cancelEditing() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

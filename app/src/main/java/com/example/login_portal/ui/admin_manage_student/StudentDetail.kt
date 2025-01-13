package com.example.login_portal.ui.admin_manage_student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.login_portal.databinding.FragmentAdminUpdateDetailInfoBinding

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

        // Observe and update UI
        viewModel.selectedStudent.observe(viewLifecycleOwner) { student ->
            binding.infoDetailFragTvDobLabel.text = student?.dateOfBirth
            binding.infoDetailFragTvNationalityLabel.text = student?.nationality
            binding.infoDetailFragTvEthnicityLabel.text = student?.ethnicity
            binding.infoDetailFragTvIdCardLabel.text = student?.identityCardNumber
            binding.infoDetailFragTvEmailLabel.text = student?.schoolEmail
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

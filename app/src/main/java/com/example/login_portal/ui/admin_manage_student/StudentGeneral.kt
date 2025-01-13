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

        // Observe and update UI
        viewModel.selectedStudent.observe(viewLifecycleOwner) { student ->
            binding.infoGeneralFragTvFullNameLabel.text = student?.fullName
            binding.infoGeneralFragTvStudentIdLabel.text = student?.studentId
            binding.infoGeneralFragTvMajorLabel.text = student?.majorId
            binding.infoGeneralFragTvAcademicProgramLabel.text = student?.academicProgram
            binding.infoGeneralFragTvGenderLabel.text = student?.gender
        }

        setupViewSwitchers()
        return binding.root
    }

    private fun setupViewSwitchers() {
        binding.infoGeneralFragSwitchToEditMode.setOnClickListener { viewModel.startEditing() }
        binding.infoGeneralFragAcceptChanges.setOnClickListener { viewModel.acceptChanges() }
        binding.infoGeneralFragCancelChanges.setOnClickListener { viewModel.cancelEditing() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

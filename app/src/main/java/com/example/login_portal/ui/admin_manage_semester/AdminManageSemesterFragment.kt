package com.example.login_portal.ui.admin_manage_semester

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentAdminManageSemesterBinding
import com.example.login_portal.ui.admin_manage_semester.SemesterAdapter
import android.content.Intent


class AdminManageSemesterFragment : Fragment() {
    private var _binding: FragmentAdminManageSemesterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AdminManageSemesterViewModel
    private lateinit var adapter: SemesterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminManageSemesterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AdminManageSemesterViewModel::class.java)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = SemesterAdapter(emptyList()) { semester ->
            startActivity(
                Intent(requireContext(), SemesterDetailActivity::class.java).apply {
                    putExtra("SEMESTER_ID", semester.id)
                    putExtra("IS_EDIT_MODE", true)
                }
            )
        }
        binding.semesterListRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.semesters.observe(viewLifecycleOwner) { semesters ->
            adapter.updateSemesters(semesters ?: emptyList())
        }
    }

    private fun setupListeners() {
        binding.fabCreateSemester.setOnClickListener {
            startActivity(
                Intent(requireContext(), SemesterDetailActivity::class.java).apply {
                    putExtra("IS_EDIT_MODE", false)
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchSemesters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
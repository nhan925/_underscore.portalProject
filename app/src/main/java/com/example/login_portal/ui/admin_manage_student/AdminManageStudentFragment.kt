package com.example.login_portal.ui.admin_manage_student

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_portal.databinding.FragmentAdminManageStudentBinding
import androidx.appcompat.widget.SearchView

class AdminManageStudentFragment : Fragment() {

    private var _binding: FragmentAdminManageStudentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AdminManageStudentViewModel
    private lateinit var studentAdapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AdminManageStudentViewModel::class.java)
        _binding = FragmentAdminManageStudentBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSearchView()

        viewModel.studentList.observe(viewLifecycleOwner) { students ->
            studentAdapter.submitList(students)
        }

        viewModel.fetchStudentList()

        return binding.root
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter { student ->
            val intent = Intent(requireContext(), EditStudentInfoActivity::class.java).apply {
                putExtra("STUDENT_ID", student.studentId)
            }
            startActivity(intent)
        }
        binding.studentListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.studentListRecyclerView.adapter = studentAdapter
    }

    private fun setupSearchView() {
        // Ensure the SearchView is not iconified by default
        binding.studentSearchView.isIconified = false
        binding.studentSearchView.clearFocus() // Prevent focus at startup

        // Handle search query input
        binding.studentSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchStudent(query)
                hideKeyboard() // Hide the keyboard after submission
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchStudent(newText)
                return true
            }
        })

        // Ensure the keyboard is shown when the SearchView gains focus
        binding.studentSearchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard()
            } else {
                hideKeyboard()
            }
        }
    }

    // Helper function to show the keyboard
    private fun showKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.studentSearchView.findFocus(), InputMethodManager.SHOW_IMPLICIT)
    }

    // Helper function to hide the keyboard
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

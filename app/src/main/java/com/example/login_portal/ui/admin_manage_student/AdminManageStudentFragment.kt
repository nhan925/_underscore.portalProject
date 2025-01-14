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
import androidx.core.widget.doOnTextChanged
import java.text.Normalizer


class AdminManageStudentFragment : Fragment() {

    private var _binding: FragmentAdminManageStudentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AdminManageStudentViewModel
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var searchAdapter: StudentAdapter
    private var originalStudents: List<StudentInfo> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AdminManageStudentViewModel::class.java)
        _binding = FragmentAdminManageStudentBinding.inflate(inflater, container, false)

        setupMainRecyclerView()
        setupSearchView()

        viewModel.studentList.observe(viewLifecycleOwner) { students ->
            studentAdapter.submitList(students)
            originalStudents = students
        }

        viewModel.fetchStudentList()

        return binding.root
    }

    private fun setupMainRecyclerView() {
        studentAdapter = StudentAdapter { student ->
            val intent = Intent(requireContext(), EditStudentInfoActivity::class.java).apply {
                putExtra("STUDENT_ID", student.studentId)
            }
            startActivity(intent)
        }
        binding.studentListRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
        }
    }

    private fun setupSearchView() {
        val searchBar = binding.studentSearchbar
        val searchView = binding.studentSearchview
        val searchRecyclerView = binding.studentSearchRecyclerView

        // Setup search view with search bar
        searchView.setupWithSearchBar(searchBar)

        // Setup search recycler view
        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchAdapter = StudentAdapter { student ->
            val intent = Intent(requireContext(), EditStudentInfoActivity::class.java).apply {
                putExtra("STUDENT_ID", student.studentId)
            }
            startActivity(intent)
            searchView.hide()
        }
        searchRecyclerView.adapter = searchAdapter

        // Handle search bar click
        searchBar.setOnClickListener {
            originalStudents = viewModel.studentList.value ?: listOf()
            searchAdapter.submitList(originalStudents)
            searchView.show()
        }

        // Handle text changes in search view
        searchView.editText.doOnTextChanged { text, _, _, _ ->
            if (text.toString().isEmpty()) {
                searchAdapter.submitList(originalStudents)
            } else {
                val normalizedQuery = text.toString().removeAccents()
                val tokens = normalizedQuery.split(" ")
                val filteredStudents = originalStudents.filter { student ->
                    tokens.all { token ->
                        student.fullName.removeAccents().contains(token, ignoreCase = true) ||
                                student.studentId.contains(token, ignoreCase = true)
                    }
                }
                searchAdapter.submitList(filteredStudents)
            }
        }
    }

    // Extension function to remove accents from text
    private fun String.removeAccents(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
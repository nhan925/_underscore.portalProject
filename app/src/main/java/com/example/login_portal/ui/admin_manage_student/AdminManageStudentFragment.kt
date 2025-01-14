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
    private var originalStudents: List<StudentInfo> = listOf()

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
            originalStudents = students
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
        binding.studentListRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
        }
    }

    private fun setupSearchView() {
        binding.studentSearchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboard()
                    return true
                }

                override fun onQueryTextChange(text: String?): Boolean {
                    if (text.isNullOrEmpty()) {
                        studentAdapter.submitList(originalStudents)
                    } else {
                        val normalizedQuery = text.removeAccents()
                        val tokens = normalizedQuery.split(" ")
                        val filteredStudents = originalStudents.filter { student ->
                            tokens.all { token ->
                                student.fullName.removeAccents().contains(token, ignoreCase = true) ||
                                        student.studentId.contains(token, ignoreCase = true)
                            }
                        }
                        studentAdapter.submitList(filteredStudents)
                    }
                    return true
                }
            })

            setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    showKeyboard()
                } else {
                    hideKeyboard()
                }
            }
        }
    }

    private fun String.removeAccents(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
    }

    private fun showKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.studentSearchView.findFocus()?.let { view ->
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.studentSearchView.findFocus()?.let { view ->
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
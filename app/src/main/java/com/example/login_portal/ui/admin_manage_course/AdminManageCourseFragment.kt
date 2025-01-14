package com.example.login_portal.ui.admin_manage_course

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentAdminManageCourseBinding
import android.content.Intent
import android.widget.SearchView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import java.text.Normalizer
import android.content.Context
import android.view.inputmethod.InputMethodManager




class AdminManageCourseFragment : Fragment() {
    private var _binding: FragmentAdminManageCourseBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CourseViewModel
    private lateinit var adapter: CourseAdapter
    private var originalCourses = listOf<Course>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminManageCourseBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(CourseViewModel::class.java)

        setupRecyclerView()
        setupSearchView()
        setupObservers()
        setupListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = CourseAdapter(emptyList()) { course ->
            startActivity(
                Intent(requireContext(), CourseDetailActivity::class.java).apply {
                    putExtra("COURSE_ID", course.id)
                    putExtra("IS_EDIT_MODE", true)
                }
            )
        }
        binding.courseRecyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        binding.courseSearchView.apply {
            setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboard()
                    return true
                }

                override fun onQueryTextChange(text: String?): Boolean {
                    if (text.isNullOrEmpty()) {
                        adapter.updateCourses(originalCourses)
                    } else {
                        val normalizedQuery = text.removeAccents()
                        val tokens = normalizedQuery.split(" ")
                        val filteredCourses = originalCourses.filter { course ->
                            tokens.all { token ->
                                course.name.removeAccents().contains(token, ignoreCase = true) ||
                                        course.id.contains(token, ignoreCase = true)
                            }
                        }
                        adapter.updateCourses(filteredCourses)
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

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.courseSearchView.windowToken, 0)
    }

    private fun showKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.courseSearchView, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupObservers() {
        viewModel.courses.observe(viewLifecycleOwner) { courses ->
            adapter.updateCourses(courses)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Add loading indicator if needed
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.fabAddCourse.setOnClickListener {
            startActivity(
                Intent(requireContext(), CourseDetailActivity::class.java).apply {
                    putExtra("IS_EDIT_MODE", false)
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchCourses()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
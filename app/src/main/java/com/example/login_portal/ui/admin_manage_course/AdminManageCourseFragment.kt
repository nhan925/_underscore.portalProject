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
            queryHint = getString(R.string.search_course_hint)

            setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboard()
                    return true
                }

                override fun onQueryTextChange(text: String?): Boolean {
                    filterCourses(text)
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

    private fun filterCourses(query: String?) {
        if (query.isNullOrBlank()) {
            adapter.updateCourses(originalCourses)
            return
        }

        val searchText = query.removeAccents().lowercase().trim()

        val filteredList = originalCourses.filter { course ->
            course.name.removeAccents().lowercase().contains(searchText) ||
                    course.id.lowercase().contains(searchText)
        }

        adapter.updateCourses(filteredList)
    }

    private fun setupObservers() {
        viewModel.courses.observe(viewLifecycleOwner) { courses ->
            originalCourses = courses
            adapter.updateCourses(courses)

            // If there's an active search, reapply the filter
            val currentQuery = binding.courseSearchView.query?.toString()
            if (!currentQuery.isNullOrBlank()) {
                filterCourses(currentQuery)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.courseSearchView.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
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
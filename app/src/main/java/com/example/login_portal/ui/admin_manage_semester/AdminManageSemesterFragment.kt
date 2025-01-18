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
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import java.text.Normalizer

class AdminManageSemesterFragment : Fragment() {
    private var _binding: FragmentAdminManageSemesterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AdminManageSemesterViewModel
    private lateinit var adapter: SemesterAdapter
    private var originalSemesters = listOf<Semester>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminManageSemesterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AdminManageSemesterViewModel::class.java)

        setupRecyclerView()
        setupSearchView()
        setupObservers()
        setupListeners()

        return binding.root
    }

    private fun setupSearchView() {
        binding.semesterSearchView.apply {
            queryHint = getString(R.string.search_semester_hint)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboard()
                    return true
                }

                override fun onQueryTextChange(text: String?): Boolean {
                    if (text.isNullOrEmpty()) {
                        adapter.updateSemesters(originalSemesters)
                    } else {
                        val normalizedQuery = text.removeAccents()
                        val tokens = normalizedQuery.split(" ")
                        val filteredSemesters = originalSemesters.filter { semester ->
                            tokens.all { token ->
                                semester.year.removeAccents().contains(token, ignoreCase = true) ||
                                        semester.semesterNum.toString().contains(token, ignoreCase = true)
                            }
                        }
                        adapter.updateSemesters(filteredSemesters)
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
        imm.hideSoftInputFromWindow(binding.semesterSearchView.windowToken, 0)
    }

    private fun showKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.semesterSearchView, InputMethodManager.SHOW_IMPLICIT)
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
            originalSemesters = semesters
            adapter.updateSemesters(semesters)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading state if needed
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                // Handle error if needed
            }
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
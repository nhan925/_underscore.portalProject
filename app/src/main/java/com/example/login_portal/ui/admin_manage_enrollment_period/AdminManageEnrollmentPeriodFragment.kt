// AdminManageEnrollmentPeriodFragment.kt
package com.example.login_portal.ui.admin_manage_enrollment_period

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.databinding.FragmentAdminManageEnrollmentPeriodBinding
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import com.example.login_portal.R


class AdminManageEnrollmentPeriodFragment : Fragment() {
    private var _binding: FragmentAdminManageEnrollmentPeriodBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RegistrationPeriodViewModel
    private lateinit var adapter: RegistrationPeriodAdapter
    private var originalPeriods = listOf<RegistrationPeriod>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminManageEnrollmentPeriodBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[RegistrationPeriodViewModel::class.java]

        setupRecyclerView()
        setupSearchView()
        setupObservers()
        setupListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = RegistrationPeriodAdapter(emptyList()) { period ->
            startActivity(
                Intent(requireContext(), RegistrationPeriodDetailActivity::class.java).apply {
                    putExtra("IS_EDIT_MODE", true)
                    putExtra("PERIOD_ID", period.id)
                }
            )
        }
        binding.periodRecyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        binding.periodSearchView.apply {
            queryHint = getString(R.string.search_period_hint)

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboard()
                    return true
                }

                override fun onQueryTextChange(text: String?): Boolean {
                    filterPeriods(text)
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

    private fun filterPeriods(query: String?) {
        if (query.isNullOrBlank()) {
            adapter.updatePeriods(originalPeriods)
            return
        }

        val searchText = query.lowercase().trim()

        val filteredList = originalPeriods.filter { period ->
            period.name.lowercase().contains(searchText)
        }

        adapter.updatePeriods(filteredList)
    }

    private fun setupObservers() {
        viewModel.registrationPeriods.observe(viewLifecycleOwner) { periods ->
            originalPeriods = periods
            adapter.updatePeriods(periods)

            // If there's an active search, reapply the filter
            val currentQuery = binding.periodSearchView.query?.toString()
            if (!currentQuery.isNullOrBlank()) {
                filterPeriods(currentQuery)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.periodSearchView.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.fabAddPeriod.setOnClickListener {
            startActivity(
                Intent(requireContext(), RegistrationPeriodDetailActivity::class.java).apply {
                    putExtra("IS_EDIT_MODE", false)
                }
            )
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.periodSearchView.windowToken, 0)
    }

    private fun showKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.periodSearchView, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchRegistrationPeriods()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.login_portal.ui.admin_manage_class

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_portal.databinding.FragmentAdminManageClassBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AdminManageClassFragment : Fragment() {
    private var _binding: FragmentAdminManageClassBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminManageClassViewModel by viewModels()
    private lateinit var classAdapter: ClassAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminManageClassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        classAdapter = ClassAdapter { classInfo ->
            navigateToClassDetails(classInfo)
        }

        binding.courseRecyclerView.apply {
            adapter = classAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupSearchView() {
        binding.classSearchView.apply {
            queryHint = "Search classes..."
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { viewModel.searchClasses(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { viewModel.searchClasses(it) }
                    return true
                }
            })
        }
    }

    private fun setupFab() {
        binding.addClassFab.apply {
            text = "Add Class"
            setOnClickListener {
                startActivity(Intent(requireContext(), CreateClassActivity::class.java))
            }
        }
    }

    private fun observeViewModel() {
        viewModel.classes.observe(viewLifecycleOwner) { classes ->
            classes?.let {
                classAdapter.updateClasses(it)
                updateEmptyState(it.isEmpty())
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            when (error) {
                is AdminManageClassViewModel.ErrorState.Error -> {
                    showError(error.message)
                    viewModel.clearError()
                }
                else -> {}
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.courseRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun navigateToClassDetails(classInfo: ClassInfo) {
        try {
            startActivity(Intent(requireContext(), ClassDetailsActivity::class.java).apply {
                putExtra("class_id", classInfo.classId)
            })
        } catch (e: Exception) {
            Log.e("AdminManageClass", "Error navigating to details: ${e.message}")
            showError("Unable to open class details")
        }
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
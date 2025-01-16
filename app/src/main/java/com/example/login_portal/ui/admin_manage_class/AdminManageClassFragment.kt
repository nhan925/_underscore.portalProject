package com.example.login_portal.ui.admin_manage_class

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentAdminManageClassBinding
import com.example.login_portal.databinding.LayoutAddClassOptionsBinding
import com.example.login_portal.databinding.LayoutExcelImportDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator



class AdminManageClassFragment : Fragment() {
    private var _binding: FragmentAdminManageClassBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminManageClassViewModel by viewModels()
    private lateinit var classAdapter: ClassAdapter
    private var importDialog: AlertDialog? = null

    // File picker for Excel
    private val pickExcelFile = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setSelectedExcelFile(it)
            updateImportDialogWithFile(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            _binding = FragmentAdminManageClassBinding.inflate(inflater, container, false)

            // Setup RecyclerView
            binding.courseRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = ClassAdapter { /* Your click handler */ }
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }

            // Observe ViewModel
            observeViewModel()

            return binding.root
        } catch (e: Exception) {
            Log.e("AdminManageClass", "Error creating view: ${e.message}")
            Toast.makeText(context, "Error initializing view", Toast.LENGTH_SHORT).show()
            throw e
        }
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

    private fun navigateToClassDetails(classInfo: ClassInfo) {
        startActivity(
            Intent(requireContext(), ClassDetailsActivity::class.java).apply {
                putExtra("class_id", classInfo.classId)
            }
        )
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
            text = "Add Class" // If using ExtendedFloatingActionButton
            setOnClickListener { showAddClassOptions() }
        }
    }

    private fun showAddClassOptions() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = LayoutAddClassOptionsBinding.inflate(layoutInflater)

        dialogBinding.apply {
            // Add single class option
            addSingleClassCard.setOnClickListener {
                startActivity(Intent(requireContext(), CreateClassActivity::class.java))
                dialog.dismiss()
            }

            // Add via Excel option
            addViaExcelCard.setOnClickListener {
                showImportExcelDialog()
                dialog.dismiss()
            }
        }

        dialog.setContentView(dialogBinding.root)
        dialog.show()
    }

    private fun showImportExcelDialog() {
        val dialogBinding = LayoutExcelImportDialogBinding.inflate(layoutInflater)

        importDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.apply {
            // Initially disable import button
            importButton.isEnabled = false

            selectFileButton.setOnClickListener {
                pickExcelFile.launch("application/vnd.ms-excel")
            }

            importButton.setOnClickListener {
                viewModel.importClassesFromExcel(requireContext())
                importDialog?.dismiss()
                showImportProgress()
            }

            cancelButton.setOnClickListener {
                importDialog?.dismiss()
            }
        }

        importDialog?.show()
    }

    private fun updateImportDialogWithFile(uri: Uri) {
        val fileName = getFileName(uri)
        importDialog?.findViewById<TextView>(R.id.selectedFileText)?.text = fileName
        importDialog?.findViewById<View>(R.id.importButton)?.isEnabled = true
    }

    private fun showImportProgress() {
        val progressDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Importing Classes")
            .setView(LinearProgressIndicator(requireContext()).apply {
                isIndeterminate = true
            })
            .setCancelable(false)
            .create()
        progressDialog.show()

        // Observe import progress
        viewModel.importProgress.observe(viewLifecycleOwner) { progress ->
            progressDialog.dismiss()
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "Unknown file"
    }

    private fun observeViewModel() {
        viewModel.classes.observe(viewLifecycleOwner) { classes ->
            classes?.let { classAdapter.updateClasses(it) }
            binding.courseRecyclerView.visibility = if (classes.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it.toString())
                viewModel.clearError() // Reset error state
            }
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
        importDialog = null
        _binding = null
    }
}
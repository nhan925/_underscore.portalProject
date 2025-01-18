package com.example.login_portal.ui.admin_manage_class

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.material.textfield.TextInputLayout
import org.apache.poi.sl.draw.geom.Context
import java.text.Normalizer




class AdminManageClassFragment : Fragment() {
    private var _binding: FragmentAdminManageClassBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminManageClassViewModel by viewModels()
    private lateinit var classAdapter: ClassAdapter
    private var importDialog: MaterialAlertDialogBuilder? = null
    private var currentExcelDialog: androidx.appcompat.app.AlertDialog? = null

    private val pickExcelFile = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                Log.d("ExcelPicker", "Selected URI: $uri")
                val fileName = getFileName(uri)
                Log.d("ExcelPicker", "Selected File Name: $fileName")

                if (isExcelFile(fileName)) {
                    viewModel.setSelectedExcelFile(uri)
                    updateImportDialogWithFile(uri)
                } else {
                    showError(getString(R.string.error_invalid_file))
                }
            } catch (e: Exception) {
                Log.e("ExcelPicker", "Error processing file: ${e.message}")
                showError(getString(R.string.error_load_failed))
            }
        } else {
            Log.e("ExcelPicker", "No file selected")
            showError(getString(R.string.error_no_file))
        }
    }


    private fun isExcelFile(fileName: String): Boolean {
        return fileName.endsWith(".xlsx", ignoreCase = true) ||
                fileName.endsWith(".xls", ignoreCase = true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            _binding = FragmentAdminManageClassBinding.inflate(inflater, container, false)
            return binding.root
        } catch (e: Exception) {
            Log.e("AdminManageClass", "Error in onCreateView: ${e.message}")
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

    private fun setupSearchView() {
        binding.classSearchView.apply {
            queryHint = getString(R.string.search_classes)

            setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboard()
                    return true
                }

                override fun onQueryTextChange(text: String?): Boolean {
                    filterClasses(text)
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

    private fun filterClasses(query: String?) {
        if (query.isNullOrBlank()) {
            viewModel.resetClassFilter()
            return
        }

        val searchText = query.removeAccents().lowercase().trim()

        viewModel.filterClasses { classInfo ->
            classInfo.className.removeAccents().lowercase().contains(searchText) ||
                    classInfo.classId.lowercase().contains(searchText)
        }
    }


    private fun String.removeAccents(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.classSearchView.windowToken, 0)
    }

    private fun showKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.classSearchView, InputMethodManager.SHOW_IMPLICIT)
    }

    private val createClassLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.refreshClasses()
            }
        }


    private fun setupFab() {
        binding.addClassFab.apply {
            text = "Add Class"
            setOnClickListener {
                showAddClassOptions()
            }
        }
    }

    private fun showAddClassOptions() {
        try {
            val dialog = BottomSheetDialog(requireContext())
            val dialogBinding = LayoutAddClassOptionsBinding.inflate(layoutInflater)


            dialogBinding.apply {
                // Add single class option
                addSingleClassCard.setOnClickListener {
                    //startActivity(Intent(requireContext(), CreateClassActivity::class.java))
                    val intent = Intent(requireContext(), CreateClassActivity::class.java)
                    createClassLauncher.launch(intent)
                    dialog.dismiss()
                }

                // Add via Excel option
                addViaExcelCard.setOnClickListener {
                    showImportExcelDialog()
                    dialog.dismiss()
                }
            }

            dialog.setOnShowListener { dialogInterface ->
                val bottomSheetDialog = dialogInterface as BottomSheetDialog
                val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.layoutParams?.height = (resources.displayMetrics.heightPixels * 0.5).toInt()
            }

            dialog.setContentView(dialogBinding.root)
            dialog.show()
        } catch (e: Exception) {
            Log.e("AdminManageClass", "Error showing options dialog: ${e.message}")
            showError("Unable to show options")
        }
    }

    private fun showImportExcelDialog() {
        try {
            val dialogBinding = LayoutExcelImportDialogBinding.inflate(layoutInflater)

            currentExcelDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.root)
                .create()

            dialogBinding.apply {
                importButton.isEnabled = false

                selectFileButton.setOnClickListener {
                    pickExcelFile.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")

                }

                importButton.setOnClickListener {
                    viewModel.importClassesFromExcel(requireContext())
                    currentExcelDialog?.dismiss()
                    showImportProgress()
                }

                cancelButton.setOnClickListener {
                    currentExcelDialog?.dismiss()
                }
            }

            currentExcelDialog?.show()
        } catch (e: Exception) {
            Log.e("AdminManageClass", "Error showing import dialog: ${e.message}")
            showError("Unable to show import dialog")
        }
    }

    private fun updateImportDialogWithFile(uri: Uri) {
        try {
            val fileName = getFileName(uri)
            currentExcelDialog?.let { dialog ->
                dialog.findViewById<TextView>(R.id.selectedFileText)?.text = fileName
                dialog.findViewById<View>(R.id.importButton)?.isEnabled = true
            }
        } catch (e: Exception) {
            Log.e("AdminManageClass", "Error updating import dialog: ${e.message}")
            showError("Unable to update file information")
        }
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

        viewModel.importProgress.observe(viewLifecycleOwner) { progress ->
            if (progress.current >= progress.total) {
                progressDialog.dismiss()
                showSuccess("Import completed successfully")
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            when (error) {
                is AdminManageClassViewModel.ErrorState.Error -> {
                    progressDialog.dismiss()
                    showError(error.message)
                }
                else -> {}
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        try {
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
        } catch (e: Exception) {
            Log.e("AdminManageClass", "Error getting file name: ${e.message}")
        }
        return result ?: uri.path?.substringAfterLast('/') ?: "Unknown file"
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
        try {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        } catch (e: Exception) {
            Log.e("AdminManageClass", "Error showing error dialog: ${e.message}")
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showSuccess(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentExcelDialog?.dismiss()
        currentExcelDialog = null
        importDialog = null
        _binding = null
    }
}
package com.example.login_portal.ui.scholarship

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentScholarshipBinding
import com.example.login_portal.databinding.FragmentScholarshipBottomSheetBinding
import com.example.login_portal.utils.SupabaseService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch


class ScholarshipBottomSheetFragment(val scholarship: Scholarship) : BottomSheetDialogFragment() {
    private var _binding: FragmentScholarshipBottomSheetBinding? = null
    private val binding get() = _binding!!
    var fileUri: Uri? = null

    private val PICK_PDF_REQUEST = 1  // Request code for PDF selection
    private val MAX_FILE_SIZE_MB = 10  // Maximum file size in MB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScholarshipBottomSheetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.scholarshipBsheetContainer)

        // Make the bottom sheet fully expanded initially
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isHideable = true

        // Button click listener to choose the file
        binding.scholarshipBsheetChooseFile.setOnClickListener {
            onChooseFileClick()
        }

        binding.scholarshipBsheetApply.setOnClickListener {
            onSubmitClick()
        }

        return root
    }

    // Trigger the file picker for PDF only
    private fun onChooseFileClick() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf" // Only allow PDF files
        startActivityForResult(intent, PICK_PDF_REQUEST)
    }

    // Handle the result of file picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK) {
            fileUri = data?.data
            if (fileUri != null) {
                // Get the file name
                val fileName = getFileName(fileUri!!)

                // Check file size
                val fileSizeInBytes = getFileSize(fileUri!!)
                if (fileSizeInBytes > MAX_FILE_SIZE_MB * 1024 * 1024) {
                    // File is too large
                    Toast.makeText(context, getString(R.string.scholarship_bsheet_fileMax), Toast.LENGTH_SHORT).show()
                } else {
                    // File is valid
                    binding.scholarshipBsheetFileName.text = fileName
                    binding.scholarshipBsheetApply.isEnabled = true  // Enable the submit button
                }
            }
        }
    }

    // Function to extract file name from URI
    private fun getFileName(uri: Uri): String {
        val cursor = context?.contentResolver?.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val fileName = columnIndex?.let { cursor.getString(it) } ?: "Unknown"
        cursor?.close()
        return fileName
    }

    // Function to get file size in bytes
    private fun getFileSize(uri: Uri): Long {
        val cursor = context?.contentResolver?.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val sizeIndex = cursor?.getColumnIndex(OpenableColumns.SIZE)
        val fileSize = sizeIndex?.let { cursor.getLong(it) } ?: 0L
        cursor?.close()
        return fileSize
    }

    private fun onSubmitClick() {
        // Check if fileUri is not null
        fileUri?.let { uri ->
            (activity as ScholarshipDetailActivity).loadingOverlay.visibility = View.VISIBLE

            // Launch a coroutine to handle file upload
            lifecycleScope.launch {
                try {
                    // Hide the bottom sheet without dismissing
                    binding.scholarshipBsheetContainer.visibility = View.GONE

                    // Upload the file and get the URL or handle success
                    val fileUrl = ScholarshipDao.uploadFile(requireContext(), uri)
                    if (fileUrl != null) {
                        ScholarshipDao.applyScholarship(scholarship.Id, fileUrl) { response ->
                            if (response == "SUCCESS") {
                                Toast.makeText(activity, activity?.getString(R.string.scholarship_bsheet_applySuccess), Toast.LENGTH_SHORT).show()
                                activity?.finish()
                            }
                            else {
                                Toast.makeText(activity, activity?.getString(R.string.scholarship_bsheet_error), Toast.LENGTH_SHORT).show()
                                (activity as ScholarshipDetailActivity).loadingOverlay.visibility = View.GONE
                                binding.scholarshipBsheetContainer.visibility = View.VISIBLE
                                dismiss()
                            }
                        }
                    } else {
                        // Handle failed upload
                        Toast.makeText(activity, activity?.getString(R.string.scholarship_bsheet_error), Toast.LENGTH_SHORT).show()
                        (activity as ScholarshipDetailActivity).loadingOverlay.visibility = View.GONE
                        binding.scholarshipBsheetContainer.visibility = View.VISIBLE
                        dismiss()
                    }
                } catch (e: Exception) {
                    // Handle exceptions, if any
                    Toast.makeText(activity, activity?.getString(R.string.scholarship_bsheet_error), Toast.LENGTH_SHORT).show()
                    (activity as ScholarshipDetailActivity).loadingOverlay.visibility = View.GONE
                    binding.scholarshipBsheetContainer.visibility = View.VISIBLE
                    dismiss()
                }
            }
        } ?: run {
            Toast.makeText(activity, activity?.getString(R.string.scholarship_bsheet_noFileSelected), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ScholarshipBottomSheet"
    }
}
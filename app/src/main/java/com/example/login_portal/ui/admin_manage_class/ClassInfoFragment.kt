package com.example.login_portal.ui.admin_manage_class
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.login_portal.databinding.FragmentClassInfoBinding


class ClassInfoFragment : Fragment() {
    private var _binding: FragmentClassInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClassDetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassInfoBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropdowns()
        setupButtons()
    }

    private fun setupDropdowns() {
        // Setup similar to CreateClassActivity dropdowns
        viewModel.registrationPeriods.observe(viewLifecycleOwner) { periods ->
            // Setup registration period dropdown
        }
        // Setup other dropdowns similarly
    }

    private fun setupButtons() {
        binding.updateButton.setOnClickListener {
            if (validateInputs()) {
                updateClassInfo()
            }
        }

        binding.deleteButton.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun updateClassInfo() {
        viewModel.updateClass(
            onSuccess = {
                Toast.makeText(context, "Class updated successfully", Toast.LENGTH_SHORT).show()
            },
            onError = { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Class")
            .setMessage("Are you sure you want to delete this class?")
            .setPositiveButton("Delete") { _, _ ->
                deleteClass()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteClass() {
        viewModel.deleteClass(
            onSuccess = {
                Toast.makeText(context, "Class deleted successfully", Toast.LENGTH_SHORT).show()
                activity?.finish()
            },
            onError = { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun validateInputs(): Boolean {
        // Add validation logic
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
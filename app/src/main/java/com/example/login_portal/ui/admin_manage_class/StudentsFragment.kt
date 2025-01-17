package com.example.login_portal.ui.admin_manage_class

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentClassStudentsBinding
import com.example.login_portal.databinding.DialogAddStudentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class StudentsFragment : Fragment() {
    private var _binding: FragmentClassStudentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClassDetailsViewModel by activityViewModels()
    private lateinit var studentAdapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassStudentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter { student ->
            showDeleteStudentDialog(student)
        }
        binding.studentsRecyclerView.apply {
            adapter = studentAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupFab() {
        binding.addStudentFab.setOnClickListener {
            showAddStudentDialog()
        }
    }

    private fun loadStudents() {
        val classInfo = viewModel.classInfo.value
        if (classInfo != null) {
            showLoading(true)
            ClassDAO.getStudentsInClass(classInfo.classId) { students ->
                if (isAdded) {
                    showLoading(false)
                    if (students != null) {
                        studentAdapter.updateStudents(students)
                        updateEmptyState(students.isEmpty())
                    } else {
                        showError(getString(R.string.error_load_failed))
                    }
                }
            }
        }
    }

    private fun showAddStudentDialog() {
        val dialogBinding = DialogAddStudentBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.add_student))
            .setView(dialogBinding.root)
            .create()

        dialogBinding.apply {
            addButton.setOnClickListener {
                val studentId = studentIdInput.text.toString().trim()
                if (studentId.isNotEmpty()) {
                    addStudent(studentId)
                    dialog.dismiss()
                } else {
                    studentIdInput.error = getString(R.string.error_enter_student_id)
                }
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun addStudent(studentId: String) {
        val classInfo = viewModel.classInfo.value
        if (classInfo != null) {
            showLoading(true)
            ClassDAO.manageStudentInClass("add", classInfo.classId, studentId) { success ->
                if (isAdded) {
                    showLoading(false)
                    if (success) {
                        showSuccess(getString(R.string.msg_student_added))
                        loadStudents()
                    } else {
                        showError(getString(R.string.error_add_student))
                    }
                }
            }
        }
    }

    private fun showDeleteStudentDialog(student: Student) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_remove_student))
            .setMessage(getString(R.string.msg_confirm_remove_student, student.fullName))
            .setPositiveButton(getString(R.string.remove)) { _, _ ->
                removeStudent(student)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun removeStudent(student: Student) {
        val classInfo = viewModel.classInfo.value
        if (classInfo != null) {
            showLoading(true)
            ClassDAO.manageStudentInClass("remove", classInfo.classId, student.studentId) { success ->
                if (isAdded) {
                    showLoading(false)
                    if (success) {
                        showSuccess(getString(R.string.msg_student_removed))
                        loadStudents()
                        refreshGrades()
                    } else {
                        showError(getString(R.string.error_remove_student))
                    }
                }
            }
        }
    }

    private fun refreshGrades() {
        val parentFragmentManager = parentFragmentManager
        val gradesFragment = parentFragmentManager.fragments.find { it is GradesFragment } as? GradesFragment
        gradesFragment?.reloadGrades()
    }


    private fun observeViewModel() {
        viewModel.classInfo.observe(viewLifecycleOwner) { classInfo ->
            classInfo?.let { loadStudents() }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.addStudentFab.isEnabled = !isLoading
        binding.studentsRecyclerView.isEnabled = !isLoading
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateText?.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.studentsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Retry") { loadStudents() }
            .show()
    }

    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

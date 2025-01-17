package com.example.login_portal.ui.admin_manage_class
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentClassGradesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView


class GradesFragment : Fragment() {
    private var _binding: FragmentClassGradesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClassDetailsViewModel by activityViewModels()
    private val studentGrades = mutableMapOf<String, StudentGradeInputs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassGradesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadStudentGrades()
        setupSaveButton()
    }

    private fun loadStudentGrades() {
        viewModel.classInfo.value?.let { classInfo ->
            ClassDAO.getStudentGrades(classInfo.classId) { grades ->
                if (grades != null) {
                    populateGradesTable(grades)
                } else {
                    Toast.makeText(context, getString(R.string.error_load_grades), Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    fun reloadGrades() {
        viewModel.classInfo.value?.let { classInfo ->
            ClassDAO.getStudentGrades(classInfo.classId) { grades ->
                if (grades != null) {
                    populateGradesTable(grades) // Refresh the grades table
                } else {
                    Toast.makeText(context, getString(R.string.error_reload_grades), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun populateGradesTable(grades: List<StudentGrade>) {
        studentGrades.clear()
        binding.gradesTable.removeAllViews()

        // Add header row
        addHeaderRow()

        // Add student rows
        grades.forEach { grade ->
            addStudentGradeRow(grade)
        }
    }

    private fun addHeaderRow() {
        val headerRow = TableRow(context).apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
        }

        val headers = listOf(
            R.string.header_student_id,
            R.string.header_student_name,
            R.string.header_grade,
            R.string.header_feedback,
            R.string.header_note
        ).map { getString(it) }

        headers.forEach { headerText ->
            val textView = TextView(context).apply {
                text = headerText
                setPadding(16, 16, 16, 16)
                setTextColor(Color.BLACK)
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
            }
            headerRow.addView(textView)
        }

        binding.gradesTable.addView(headerRow)
    }

    private fun addStudentGradeRow(grade: StudentGrade) {
        val row = TableRow(context).apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
        }

        // Student ID
        addTextView(row, grade.studentId)

        // Student Name
        addTextView(row, grade.fullName)

        // Grade Input
        val gradeInput = EditText(context).apply {
            setText(grade.grade?.toString() ?: "")
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            filters = arrayOf(InputFilter.LengthFilter(4))
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
        }
        row.addView(gradeInput)

        // Feedback Input
        val feedbackInput = EditText(context).apply {
            setText(grade.feedback ?: "")
            inputType = InputType.TYPE_CLASS_TEXT
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
        }
        row.addView(feedbackInput)

        // Note Input
        val noteInput = EditText(context).apply {
            setText(grade.note ?: "")
            inputType = InputType.TYPE_CLASS_TEXT
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
        }
        row.addView(noteInput)

        binding.gradesTable.addView(row)

        studentGrades[grade.studentId] = StudentGradeInputs(
            grade.studentId,
            gradeInput,
            feedbackInput,
            noteInput
        )
    }

    private fun addTextView(row: TableRow, text: String) {
        val textView = TextView(context).apply {
            this.text = text
            setPadding(16, 16, 16, 16)
            setTextColor(Color.BLACK)
            textSize = 14f
        }
        row.addView(textView)
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            if (validateGrades()) {
                saveGrades()
            }
        }
    }

    private fun validateGrades(): Boolean {
        var isValid = true
        studentGrades.values.forEach { inputs ->
            val gradeText = inputs.gradeInput.text.toString()
            if (gradeText.isNotEmpty()) {
                val grade = gradeText.toDoubleOrNull()
                if (grade == null || grade < 0 || grade > 10) {
                    inputs.gradeInput.error = getString(R.string.invalid_grade)
                    isValid = false
                }
            }
        }
        return isValid
    }

    private fun saveGrades() {
        viewModel.classInfo.value?.let { classInfo ->
            var successCount = 0
            var totalCount = studentGrades.size
            val failedStudents = mutableListOf<String>()

            studentGrades.forEach { (studentId, inputs) ->
                val grade = inputs.gradeInput.text.toString().toDoubleOrNull()
                val feedback = inputs.feedbackInput.text.toString()
                val note = inputs.noteInput.text.toString()

                if (grade != null) {
                    ClassDAO.updateGrades(
                        classId = classInfo.classId,
                        studentId = studentId,
                        grade = grade,
                        feedback = feedback.ifEmpty { null },
                        note = note.ifEmpty { null }
                    ) { success ->
                        if (success) {
                            successCount++
                        } else {
                            failedStudents.add(studentId)
                        }

                        if (successCount + failedStudents.size == totalCount) {
                            // All updates completed
                            showSaveResult(successCount, failedStudents)
                        }
                    }
                }
            }
        }
    }

    private fun showSaveResult(successCount: Int, failedStudents: List<String>) {
        val message = if (failedStudents.isEmpty()) {
            getString(R.string.msg_grades_save_success)
        } else {
            getString(R.string.msg_grades_save_partial)
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

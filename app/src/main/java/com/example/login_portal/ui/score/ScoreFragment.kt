package com.example.login_portal.ui.score

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentScoreBinding
import com.google.android.material.snackbar.Snackbar

class ScoreFragment : Fragment() {
    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ScoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScoreBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ScoreViewModel::class.java]

        viewModel.filterScores("All", "All")
        setupSpinners()
        setupClickListeners()
        setupObservers()
        initializeTable()

        return binding.root
    }

    private fun setupSpinners() {
        viewModel.availableYears.observe(viewLifecycleOwner) { years ->
            val yearAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                years
            )
            binding.yearSpinner.setAdapter(yearAdapter)

            binding.yearSpinner.setText("All", false)
            viewModel.updateAvailableSemesters("All")
        }

        viewModel.availableSemesters.observe(viewLifecycleOwner) { semesters ->
            val semesterAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                semesters
            )
            binding.semesterSpinner.setAdapter(semesterAdapter)



            if (binding.yearSpinner.text.toString() == "All") {
                binding.semesterSpinner.setText("All", false)
            }
//            } else {
//                if (semesters.size > 1) {
//                    binding.semesterSpinner.setText(semesters[1], false)
//                }
//            }
        }

        binding.yearSpinner.setOnItemClickListener { _, _, _, _ ->
            val selectedYear = binding.yearSpinner.text.toString()
            viewModel.updateAvailableSemesters(selectedYear)

            if (selectedYear == "All") {
                binding.semesterSpinner.setText("All", false)
            }

        }
    }

    private fun setupClickListeners() {
        binding.viewButton.setOnClickListener {
            val year = binding.yearSpinner.text.toString()
            val semester = binding.semesterSpinner.text.toString()
            viewModel.filterScores(year, semester)



        }

    }

    private fun setupObservers() {
        viewModel.scoreData.observe(viewLifecycleOwner) { scores ->
            updateTable(scores)
        }

        viewModel.statistics.observe(viewLifecycleOwner) { stats ->
            updateStatistics(stats)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.scoreTable.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                showSnackbar(it)
            }
        }
    }

    private fun initializeTable() {
        val headers = arrayOf(
            getString(R.string.year), getString(R.string.semester), getString(R.string.course_code),
            getString(R.string.subject), getString(R.string.credits), getString(R.string.class_code),
            getString(R.string.score10), getString(R.string.score4), getString(R.string.notes)

        )

        val headerRow = TableRow(context).apply {
            headers.forEach { headerText ->
                addView(createTableCell(headerText, true))
            }
        }

        binding.scoreTable.addView(headerRow)
    }

    private fun createTableCell(text: String, isHeader: Boolean = false): TextView {
        return TextView(context).apply {
            setText(text)
            setPadding(24, 12, 24, 12)
            gravity = android.view.Gravity.CENTER

            if (isHeader) {
                setTypeface(null, android.graphics.Typeface.BOLD)
                val params = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                params.marginEnd = 16
                layoutParams = params
            } else {
                val params = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                params.marginEnd = 16
                layoutParams = params
            }

            minimumWidth = when {
                text == getString(R.string.year) || text.matches(Regex("\\d{4}-\\d{4}")) -> 120
                text == getString(R.string.semester) || text.matches(Regex("\\d")) -> 80
                text == getString(R.string.course_code) || text.matches(Regex("CSC\\d{5}")) -> 100
                text == getString(R.string.subject) || text.matches(Regex("[A-Za-z\\s]+")) -> 150
                text == getString(R.string.credits) || text.matches(Regex("\\d+")) -> 70
                text == getString(R.string.class_code) -> 100
                text == getString(R.string.score10) || text == getString(R.string.score4) || text.matches(Regex("\\d+\\.\\d+")) -> 80
                text == getString(R.string.notes) -> 120
                else -> TableRow.LayoutParams.WRAP_CONTENT
            }
        }
    }

    private fun updateTable(scores: List<ScoreData>) {
        binding.scoreTable.removeViews(1, binding.scoreTable.childCount - 1)

        scores.forEachIndexed { index, score ->
            val row = TableRow(context).apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                setBackgroundColor(
                    if (index % 2 == 0)
                        ContextCompat.getColor(requireContext(), R.color.table_row_even)
                    else
                        ContextCompat.getColor(requireContext(), R.color.table_row_odd)
                )
            }

            with(score) {
                listOf(
                    year, semester, courseCode, subjectName, credits.toString(),
                    classCode, String.format("%.2f", score10),
                    String.format("%.2f", score4), notes
                ).forEach { cellText ->
                    row.addView(createTableCell(cellText))
                }
            }

            binding.scoreTable.addView(row)
        }
    }

    private fun updateStatistics(stats: Statistics) {
//        binding.gpa4Scale.text = String.format("4.0 Scale: %.2f", stats.gpa4Scale)
//        binding.gpa10Scale.text = String.format("10.0 Scale: %.2f", stats.gpa10Scale)
//        binding.totalCredits.text = "Credits: ${stats.totalCredits}"
//        binding.totalCourses.text = "Courses: ${stats.totalCourses}"
        binding.gpa4Scale.text = getString(R.string.score4)+ ": %.2f".format(stats.gpa4Scale)
        binding.gpa10Scale.text = getString(R.string.score10)+ ": %.2f".format(stats.gpa10Scale)
        binding.totalCredits.text =getString(R.string.num_credit)+ ": ${stats.totalCredits}"
        binding.totalCourses.text = getString(R.string.num_course)+ ": ${stats.totalCourses}"
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
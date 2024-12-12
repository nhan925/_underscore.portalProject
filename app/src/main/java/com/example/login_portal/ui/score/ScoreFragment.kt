package com.example.login_portal.ui.score

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentScoreBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

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
        setupSpeedDialMenu()
        initializeTable()

        return binding.root
    }

    private fun setupSpeedDialMenu() {
        val speedDial = binding.speedDial
        val overlay = binding.overlay


        // Lắng nghe sự kiện thay đổi trạng thái của SpeedDial
        speedDial.setOnChangeListener(object : SpeedDialView.OnChangeListener {
            override fun onMainActionSelected(): Boolean {
                // Không thực hiện hành động gì khi nhấn nút chính
                return false
            }

            override fun onToggleChanged(isOpen: Boolean) {
                // Hiển thị hoặc ẩn lớp phủ dựa trên trạng thái SpeedDial
                if (isOpen) {
                    overlay.visibility = View.VISIBLE
                } else {
                    overlay.visibility = View.GONE
                }
            }
        })

        // Thêm các hành động vào SpeedDial
        speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.action_review, R.drawable.ic_score)
                .setLabel(getString(R.string.inf47))
                .create()
        )
        speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.action_request, R.drawable.ic_lock)
                .setLabel(R.string.inf48)
                .create()
        )

        // Xử lý sự kiện khi người dùng chọn menu
        speedDial.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.action_review -> {
                    // Điều hướng đến giao diện Add Person
                    findNavController().navigate(R.id.action_nav_Score_to_nav_ReviewScore)
                    overlay.visibility = View.GONE
                    true
                }
                R.id.action_request -> {
                    // Điều hướng đến giao diện Add Alarm
                    findNavController().navigate(R.id.action_nav_Score_to_nav_RequestScore)
                    overlay.visibility = View.GONE
                    true
                }
                else -> false
            }
        }

        // Xử lý sự kiện khi người dùng nhấp vào lớp phủ
        overlay.setOnClickListener {
            speedDial.close()
            overlay.visibility = View.GONE
        }
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
                text == getString(R.string.course_code)  -> 100
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
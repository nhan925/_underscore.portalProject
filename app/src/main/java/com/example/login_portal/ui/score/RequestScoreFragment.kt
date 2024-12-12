package com.example.login_portal.ui.score

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentRequestScoreBinding
import com.example.login_portal.utils.ApiServiceHelper
import com.google.android.material.snackbar.Snackbar

class RequestScoreFragment : Fragment() {
    private var _binding: FragmentRequestScoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ScoreViewModel
    private var selectedScore: ScoreData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestScoreBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ScoreViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinners()
        setupListeners()
    }

    private fun setupSpinners() {
        // Setup Year Spinner
        viewModel.availableYears.observe(viewLifecycleOwner) { years ->
            val yearAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                years
            )
            binding.yearSpinner.setAdapter(yearAdapter)
            binding.yearSpinner.setText("All", false)
        }

        // Setup Semester Spinner
        viewModel.availableSemesters.observe(viewLifecycleOwner) { semesters ->
            val semesterAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                semesters
            )
            binding.semesterSpinner.setAdapter(semesterAdapter)
            binding.semesterSpinner.setText("All", false)
        }

        // Setup Course Spinner based on filtered scores
        viewModel.scoreData.observe(viewLifecycleOwner) { scores ->
            val courses = scores.map { "${it.courseCode} - ${it.subjectName}" }.distinct()
            val courseAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                courses
            )
            binding.courseSpinner.setAdapter(courseAdapter)
        }
    }

    private fun setupListeners() {
        // Year Spinner Change Listener
        binding.yearSpinner.setOnItemClickListener { _, _, _, _ ->
            val selectedYear = binding.yearSpinner.text.toString()
            viewModel.updateAvailableSemesters(selectedYear)
            updateCourseList()
            clearCourseDetails()
        }

        // Semester Spinner Change Listener
        binding.semesterSpinner.setOnItemClickListener { _, _, _, _ ->
            updateCourseList()
            clearCourseDetails()
        }

        // Course Spinner Change Listener
        binding.courseSpinner.setOnItemClickListener { _, _, position, _ ->
            val selectedCourseText = binding.courseSpinner.text.toString()
            val courseCode = selectedCourseText.split(" - ")[0]

            viewModel.scoreData.value?.find { it.courseCode == courseCode }?.let { score ->
                selectedScore = score
                updateCourseDetails(score)
            }
        }

        // Submit Button
        binding.submitButton.setOnClickListener {
            if (validateForm()) {
                showDialog()
            }
        }

        // Cancel Button
        binding.cancelButton.setOnClickListener {
            val slideOut = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right)
            binding.root.startAnimation(slideOut)
            slideOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    findNavController().navigateUp()
                }
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
    }

    private fun updateCourseList() {
        val year = binding.yearSpinner.text.toString()
        val semester = binding.semesterSpinner.text.toString()
        viewModel.filterScores(year, semester)
    }

    private fun updateCourseDetails(score: ScoreData) {
        binding.classCodeInput.setText(score.classCode)
        binding.currentScoreInput.setText(score.score10.toString())
    }

    private fun clearCourseDetails() {
        binding.classCodeInput.setText("")
        binding.currentScoreInput.setText("")
        binding.noteInput.setText("")
        selectedScore = null
    }

    private fun validateForm(): Boolean {
        if (selectedScore == null) {
            showError(getString(R.string.inf13))
            return false
        }

        if (binding.noteInput.text.isNullOrBlank()) {
            showError(getString(R.string.inf14))
            return false
        }

        return true
    }

    private fun submitRequest() {

        // TODO: Implement API call to submit review request
        //loading
        binding.progressBar.visibility = View.VISIBLE
        binding.root.alpha = 0.5f
        binding.submitButton.isEnabled = false

        val content = buildRequestContent()
        Log.e("submitRequest", content)

        // Data goi API
        val data = mapOf(
            "content" to content,
            "status" to getString(R.string.request_status_processing)
        )

        // Call API
        ApiServiceHelper.post("/rpc/add_request", data) { respone ->
            activity?.runOnUiThread {
                binding.progressBar.visibility = View.GONE
                binding.root.alpha = 1.0f
                binding.submitButton.isEnabled = true

                val success = respone?.contains("Request submitted successfully") == true
                if (success) {
                    showSuccess(getString(R.string.inf40))
                    findNavController().navigateUp()
                } else {
                    showError(getString(R.string.inf41))
                }
            }
        }
       // showSuccess("Gửi yêu cầu phúc khảo thành công")
       // findNavController().navigateUp()
    }

    private fun showDialog(){
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.inf15))
            .setMessage(getString(R.string.inf16))
            .setPositiveButton(getString(R.string.inf18)) { _, _ ->
                submitRequest()
            }
            .setNegativeButton(getString(R.string.inf17), null)
            .show()
    }

    private fun buildRequestContent(): String {
        val contentBuilder = StringBuilder("Phúc khảo bảng điểm\r\n")

        val year = binding.yearSpinner.text.toString()
        if ( year.isNotEmpty()) {
            contentBuilder.append("Năm học: ${formatYear(year)}\r\n")
        }
        val semester = binding.semesterSpinner.text.toString()
        if (semester.isNotEmpty()) {
            contentBuilder.append("Học kỳ: $semester\r\n")
        }
        val courseCode = binding.courseSpinner.text.toString()
        if (courseCode.isNotEmpty()) {
            contentBuilder.append("Môn học: ${courseCode.split(" - ")[1]}\r\n")
            //contentBuilder.append("Môn học: ${courseCode}\r\n")
        }
        val currentScore = binding.currentScoreInput.text.toString()
        if (currentScore.isNotEmpty()) {
            contentBuilder.append("Điểm hiện tại: $currentScore\r\n")
        }

        val classCode = binding.classCodeInput.text.toString()
        if (classCode.isNotEmpty()) {
            contentBuilder.append("Mã lớp: $classCode\r\n")
        }

        val note = binding.noteInput.text.toString()
        if (note.isNotEmpty()) {
            contentBuilder.append("Ghi chú: $note")
        }

        return contentBuilder.toString()
    }

    private fun formatYear(shortYear: String): String {
        if (shortYear == "All") return shortYear

        val years= shortYear.split("-")
        if (years.size != 2) return shortYear

        val startYear = "20${years[0].trim()}"
        val endYear = "20${years[1].trim()}"

        return "$startYear - $endYear"


    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
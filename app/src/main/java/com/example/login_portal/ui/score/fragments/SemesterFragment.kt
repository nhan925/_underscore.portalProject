package com.example.login_portal.ui.score.fragments

import android.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.databinding.TabSemesterBinding
import com.example.login_portal.ui.score.ReviewScoreFragment
import com.example.login_portal.ui.score.ScoreViewModel

class SemesterFragment : Fragment() {
    private var _binding: TabSemesterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ScoreViewModel
    private var vnCount = 0
    private var enCount = 0
    private var selectedYear = ""
    private var selectedSemester = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabSemesterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireParentFragment())[ScoreViewModel::class.java]
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinners()
        setupTextWatcher()
    }


    private fun setupSpinners() {
        viewModel.availableYears.observe(viewLifecycleOwner) { years ->
            val yearAdapter = ArrayAdapter(
                requireContext(),
                R.layout.simple_dropdown_item_1line,
                years.filter { it != "All" }
            )
            binding.yearSpinner.setAdapter(yearAdapter)
        }


        binding.yearSpinner.setOnItemClickListener { _, _, _, _ ->
            selectedYear = binding.yearSpinner.text.toString()
            viewModel.updateAvailableSemesters(selectedYear)
            updateSummary()
        }

        binding.semesterSpinner.setOnItemClickListener { _, _, _, _ ->
            selectedSemester = binding.semesterSpinner.text.toString()
            updateSummary()
        }


        viewModel.availableSemesters.observe(viewLifecycleOwner) { semesters ->
            val semesterAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                semesters.filter { it != "All" }
            )
            binding.semesterSpinner.setAdapter(semesterAdapter)
        }
    }

    private fun setupTextWatcher(){
        binding.etVietnameseSemester.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // to do
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // to do
            }
            override fun afterTextChanged(s: Editable?) {
                vnCount = s?.toString()?.toIntOrNull() ?: 0
                updateSummary()
            }
        })

        binding.etEnglishSemester.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // to do
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // to do
            }
            override fun afterTextChanged(s: Editable?) {
                enCount = s?.toString()?.toIntOrNull() ?: 0
                updateSummary()
            }

        })

    }

    private fun updateSummary(){
        (parentFragment as? ReviewScoreFragment)?.updateSummary()
    }


    fun getTranscriptInfo(): Triple<String, String, Pair<Int, Int>>? {
       return if (selectedYear.isNotEmpty() && selectedSemester.isNotEmpty()) {
           Triple(selectedYear, selectedSemester, Pair(vnCount, enCount))
       } else {
           null
       }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




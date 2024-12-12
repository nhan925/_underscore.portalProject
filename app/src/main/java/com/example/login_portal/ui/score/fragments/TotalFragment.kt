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
import com.example.login_portal.databinding.TabTotalBinding
import com.example.login_portal.ui.score.ReviewScoreFragment
import com.example.login_portal.ui.score.ScoreViewModel

class TotalFragment : Fragment() {
    private var _binding: TabTotalBinding? = null
    private val binding get() = _binding!!

    private var vnCount = 0
    private var enCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabTotalBinding.inflate(inflater, container, false)
        setupTextWatcher()
        return binding.root
    }

    private fun setupTextWatcher(){
        binding.etVietnameseTotal.addTextChangedListener(object : TextWatcher {
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

        binding.etEnglishTotal.addTextChangedListener(object : TextWatcher {
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



    fun getTranscriptCounts(): Pair<Int, Int> {
        //val vnCount = binding.etVietnameseTotal.text.toString().toIntOrNull() ?: 0
       // val enCount = binding.etEnglishTotal.text.toString().toIntOrNull() ?: 0
        return Pair(vnCount, enCount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


package com.example.login_portal.ui.score

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentRequestScoreBinding
import com.example.login_portal.databinding.FragmentScoreBinding

class RequestScoreFragment : Fragment() {
    private var _binding: FragmentRequestScoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ScoreViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_score, container, false)
    }

}
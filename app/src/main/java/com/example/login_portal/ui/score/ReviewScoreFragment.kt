package com.example.login_portal.ui.score

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentReviewScoreBinding
import com.example.login_portal.databinding.FragmentScoreBinding


class ReviewScoreFragment : Fragment() {

    private var _binding: FragmentReviewScoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ScoreViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_review_score, container, false)
    }


}
package com.example.login_portal.ui.admin_review_feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.databinding.FragmentAdminReviewFeedbackBinding

class AdminReviewFeedbackFragment : Fragment() {

    private var _binding: FragmentAdminReviewFeedbackBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val adminRvFeedbackViewModel =
            ViewModelProvider(this).get(AdminReviewFeedbackViewModel::class.java)

        _binding = FragmentAdminReviewFeedbackBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAdminReview
        adminRvFeedbackViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
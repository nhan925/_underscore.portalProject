package com.example.login_portal.ui.admin_review_feedback

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentAdminReviewFeedbackBinding
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.coroutines.launch

class AdminReviewFeedbackFragment : Fragment() {
    private lateinit var adminRvFeedbackViewModel: AdminReviewFeedbackViewModel
    private var _binding: FragmentAdminReviewFeedbackBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        setupTypeSpinner()
        reset()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = AdminReviewFeedbackViewModelFactory(requireContext())
        adminRvFeedbackViewModel = ViewModelProvider(this,factory).get(AdminReviewFeedbackViewModel::class.java)
        _binding = FragmentAdminReviewFeedbackBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupTypeSpinner()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AdapterForListFeedbackApp(emptyList())
        val recyclerView = binding.fbAppList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter.onItemClick = { item ->
            val intent = Intent(requireContext(), AdminReviewAppFeedbackDetailActivity::class.java)
            intent.putExtra("FEEDBACK_DETAIL", Gson().toJson(item))
            startActivity(intent)
        }

        adminRvFeedbackViewModel.feedbackAppList.observe(viewLifecycleOwner){ feedBackList ->
            adapter.updateData(feedBackList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupTypeSpinner() {
        val types = listOf(
            getString(R.string.review_feedback_detail_category_feedback),
            getString(R.string.review_feedback_detail_category_error)
        )
        val adapterType = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            types
        )
        binding.feedbackTypeSpinner.setAdapter(adapterType)

        if (binding.feedbackTypeSpinner.adapter != null && binding.feedbackTypeSpinner.adapter.count > 0) {
            binding.feedbackTypeSpinner.setText(adapterType.getItem(0), false)
        }

        binding.feedbackTypeSpinner.setOnItemClickListener { parent, view, position, id ->
            viewLifecycleOwner.lifecycleScope.launch {
                adminRvFeedbackViewModel.resetAppFeedbackList(position)
            }
        }
    }

    private fun reset(){
        viewLifecycleOwner.lifecycleScope.launch {
            adminRvFeedbackViewModel.resetAppFeedbackList(0)
        }
    }
}
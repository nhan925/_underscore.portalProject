package com.example.login_portal.ui.payment_history

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentPaymentHistoryBinding
import com.example.login_portal.databinding.FragmentTuitionBinding
import com.example.login_portal.ui.tuition.AdapterForListTuition
import com.example.login_portal.ui.tuition.TuitionDetailActivity
import com.example.login_portal.ui.tuition.TuitionViewModel

class PaymentHistoryFragment : Fragment() {
    private var _binding: FragmentPaymentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel = PaymentHistoryViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AdapterForListPaymentHistory(emptyList())

        val recyclerView: RecyclerView = binding.paymentHistoryRecycler
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.PaymentHistory.observe(viewLifecycleOwner) { PaymentHistoryList ->
            adapter.updateData(PaymentHistoryList)
        }

        viewModel.getAllPaymentHistory()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllPaymentHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
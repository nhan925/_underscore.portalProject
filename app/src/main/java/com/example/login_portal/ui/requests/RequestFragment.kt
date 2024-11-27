package com.example.login_portal.ui.requests

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentNotificationBinding
import com.example.login_portal.databinding.FragmentRequestBinding
import com.example.login_portal.ui.notification.NotificationViewModel
import com.example.login_portal.utils.ApiService
import com.example.login_portal.utils.ApiServiceHelper

class RequestFragment : Fragment() {

    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RequestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = RequestViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.requestRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = RequestItemAdapter(listOf(), requireContext())
        recyclerView.adapter = adapter
        viewModel.requests.observe(viewLifecycleOwner, Observer { data ->
            adapter.resetSource(data)
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
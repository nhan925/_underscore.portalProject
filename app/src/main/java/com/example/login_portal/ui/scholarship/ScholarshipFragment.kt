package com.example.login_portal.ui.scholarship

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentRequestBinding
import com.example.login_portal.databinding.FragmentScholarshipBinding
import com.example.login_portal.ui.requests.RequestDetail
import com.example.login_portal.ui.requests.RequestItemAdapter
import com.example.login_portal.ui.requests.RequestViewModel
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.gson.Gson

class ScholarshipFragment : Fragment() {
    private var _binding: FragmentScholarshipBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ScholarshipViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ScholarshipViewModel(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScholarshipBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.scholarshipRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val yearSpinner = binding.scholarshipYearSpinnerTV
        val adapter = ScholarshipItemAdapter(listOf(), requireContext())

        viewModel.scholarships.observe(viewLifecycleOwner, Observer { data ->
            adapter.resetSource(data)
            (yearSpinner as? MaterialAutoCompleteTextView)?.setSimpleItems(viewModel.years.toTypedArray())
        })
        recyclerView.adapter = adapter

        return root
    }

    override fun onResume() {
        super.onResume()
        viewModel.reset { }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
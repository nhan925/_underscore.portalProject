package com.example.login_portal.ui.course

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentCourseBinding
import com.example.login_portal.databinding.FragmentRequestBinding
import com.example.login_portal.ui.requests.RegistrationPeriodItemAdapter
import com.example.login_portal.ui.requests.RequestDetail
import com.example.login_portal.ui.requests.RequestItemAdapter
import com.example.login_portal.ui.requests.RequestViewModel
import com.google.gson.Gson


class CourseFragment : Fragment() {
    private var _binding: FragmentCourseBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CourseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = CourseViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.registrationPeriodRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = RegistrationPeriodItemAdapter(listOf(), requireContext())
        recyclerView.adapter = adapter
        adapter.onItemClick = { item ->

        }

        viewModel.periods.observe(viewLifecycleOwner, Observer { data ->
            adapter.resetSource(data)
        })

        return root
    }

    override fun onResume() {
        super.onResume()
        viewModel.reset()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
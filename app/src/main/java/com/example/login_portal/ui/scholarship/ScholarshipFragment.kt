package com.example.login_portal.ui.scholarship

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.collection.emptyLongSet
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentRequestBinding
import com.example.login_portal.databinding.FragmentScholarshipBinding
import com.example.login_portal.ui.course.Course
import com.example.login_portal.ui.course.CourseItemAdapter
import com.example.login_portal.ui.requests.RequestDetail
import com.example.login_portal.ui.requests.RequestItemAdapter
import com.example.login_portal.ui.requests.RequestViewModel
import com.example.login_portal.utils.removeAccents
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

        val viewDetails: (Scholarship) -> Unit = { scholarship ->
            val intent = Intent(requireContext(), ScholarshipDetailActivity::class.java)
            intent.putExtra("SCHOLARSHIP_DETAIL", Gson().toJson(scholarship))
            startActivity(intent)
        }


        val recyclerView = binding.scholarshipRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val yearSpinner = binding.scholarshipYearSpinnerTV
        val adapter = ScholarshipItemAdapter(listOf(), requireContext())
        adapter.onItemClick = { scholarship ->
            viewDetails.invoke(scholarship)
        }

        viewModel.scholarships.observe(viewLifecycleOwner, Observer { data ->
            adapter.resetSource(data)
        })
        recyclerView.adapter = adapter

        (yearSpinner as? MaterialAutoCompleteTextView)?.apply {
            setOnItemClickListener { parent, _, position, _ ->
                val selectedYear: String =
                    if (parent.getItemAtPosition(position) as String == getString(R.string.sholarship_all_years))
                        ""
                    else
                        parent.getItemAtPosition(position) as String
                viewModel.reset(selectedYear) {  }
            }
        }

        val searchBar = binding.scholarshipSearchbar
        val searchView = binding.scholarshipSearchview
        val searchRecyclerView = binding.scholarshipSearchRecyclerView

        searchView.setupWithSearchBar(searchBar)

        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        var originalSearchScholarships: List<Scholarship> = listOf()
        val searchAdapter = ScholarshipItemAdapter(originalSearchScholarships, requireContext())
        searchAdapter.onItemClick = { scholarship ->
            viewDetails.invoke(scholarship)
        }
        searchRecyclerView.adapter = searchAdapter

        searchBar.setOnClickListener {
            originalSearchScholarships = viewModel.scholarships.value!!
            (searchRecyclerView.adapter as ScholarshipItemAdapter).notifyDataSetChanged()
            searchView.show()
        }
        searchView.editText.doOnTextChanged { text, _, _, _ ->
            if (text.toString().isEmpty()) {
                (searchRecyclerView.adapter as ScholarshipItemAdapter).resetSource(originalSearchScholarships)
            }
            else {
                val normalizedQuery = text.toString().removeAccents()
                val tokens = normalizedQuery.split(" ")
                val filteredScholarships = originalSearchScholarships.filter { scholarship ->
                    tokens.all { token ->
                        scholarship.Name.removeAccents().contains(token)
                    }
                }
                (searchRecyclerView.adapter as ScholarshipItemAdapter).resetSource(filteredScholarships)
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        binding.scholarshipSearchview.hide()
        viewModel.reset("") {
            (binding.scholarshipYearSpinnerTV as? MaterialAutoCompleteTextView)?.apply {
                setSimpleItems(viewModel.years.toTypedArray())
                setText(viewModel.years.firstOrNull() ?: "", false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
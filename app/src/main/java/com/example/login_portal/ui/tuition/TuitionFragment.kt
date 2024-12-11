package com.example.login_portal.ui.tuition

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.databinding.FragmentTuitionBinding
import com.example.login_portal.ui.feedback_course.AdapterForListCourse
import com.example.login_portal.ui.feedback_course.FeedbackCourseActivity
import com.example.login_portal.ui.feedback_course.FeedbackCourseViewModel
import com.example.login_portal.ui.schedule.ScheduleViewModel


class TuitionFragment : Fragment() {

    private var _binding: FragmentTuitionBinding? = null
    private val binding get() = _binding!!
    private val viewModel = TuitionViewModel.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTuitionBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AdapterForListTuition(emptyList()) { tuition ->
            val intent = Intent(requireContext(), TuitionDetailActivity::class.java)
            intent.putExtra("year", tuition.year)
            intent.putExtra("semester", tuition.semester)
            intent.putExtra("totalFee", tuition.totalTuitionFee)
            intent.putExtra("totalCourse", tuition.totalCourse)
            startActivityForResult(intent, 1)
        }

        val recyclerView: RecyclerView = binding.tuitionListRecycler
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.tuitionList.observe(viewLifecycleOwner) { tuitionList ->
            adapter.updateData(tuitionList)
        }

        viewModel.getTuitionListFromDatabase()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTuitionListFromDatabase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
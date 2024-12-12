package com.example.login_portal.ui.feedback_course

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.databinding.FragmentFeedbackCourseListBinding
import com.example.login_portal.utils.ApiServiceHelper


class FeedbackCourseFragment : Fragment() {

    private var _binding: FragmentFeedbackCourseListBinding? = null
    private val binding get() = _binding!!

    private val viewModel = FeedbackCourseViewModel.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedbackCourseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title: TextView = binding.fbCourseTitle

        viewModel.getTitle(resources) { titleText ->
            title.text = titleText
        }

        val adapter = AdapterForListCourse(emptyList()) { course ->
            val intent = Intent(requireContext(), FeedbackCourseActivity::class.java)
            intent.putExtra("courseName", course.courseName)
            intent.putExtra("teacherName", course.teacherName)
            viewModel.setClassID(course.classID)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = binding.fbCourseList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.courseList.observe(viewLifecycleOwner) { courseList ->
            adapter.updateData(courseList)
        }

        viewModel.getCourseListFromDatabase()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCourseListFromDatabase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
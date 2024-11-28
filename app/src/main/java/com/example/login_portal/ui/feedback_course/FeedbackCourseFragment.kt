package com.example.login_portal.ui.feedback_course

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_portal.databinding.FragmentFeedbackCourseListBinding


class FeedbackCourseFragment : Fragment() {

    private var _binding: FragmentFeedbackCourseListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val feedbackSystemViewModel =
            ViewModelProvider(this).get(FeedbackCourseViewModel::class.java)

        _binding = FragmentFeedbackCourseListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val courseList = listOf(
            Course("Lập trình Windows", "CSC00001", "Trần Duy Quang", true),
            Course("Cơ sở dữ liệu", "CSC00002", "Nguyễn Văn A", false),
            Course("Lập trình Java", "CSC00003", "Phạm Thị B", true),
        )

        val adapter = AdapterForListCourse(courseList) { course ->
            val intent = Intent(requireContext(), FeedbackCourseActivity::class.java)
            intent.putExtra("courseName", course.name)
            intent.putExtra("teacherName", course.teacherName)
            Toast.makeText(requireContext(), "Clicked: ${course.name}", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        binding.fbCourseList.adapter = adapter
        binding.fbCourseList.layoutManager = LinearLayoutManager(requireContext())

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
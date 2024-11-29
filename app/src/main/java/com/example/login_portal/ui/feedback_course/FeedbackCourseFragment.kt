package com.example.login_portal.ui.feedback_course

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_portal.databinding.FragmentFeedbackCourseListBinding
import com.example.login_portal.utils.ApiServiceHelper


class FeedbackCourseFragment : Fragment() {

    private var _binding: FragmentFeedbackCourseListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdapterForListCourse
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

        // Khởi tạo adapter
        adapter = AdapterForListCourse(emptyList()) { course ->
            val intent = Intent(requireContext(), FeedbackCourseActivity::class.java)
            intent.putExtra("courseName", course.courseName)
            intent.putExtra("teacherName", course.teacherName)
            viewModel.setClassID(course.classID)
            Log.d("FeedbackCourseFragment", "ClassID: ${course.classID}")
            Toast.makeText(requireContext(), "Clicked: ${course.courseName}", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        // Cài đặt RecyclerView
        binding.fbCourseList.adapter = adapter
        binding.fbCourseList.layoutManager = LinearLayoutManager(requireContext())

        // Quan sát dữ liệu từ ViewModel
        viewModel.courseList.observe(viewLifecycleOwner) { courseList ->
            adapter.updateData(courseList) // Cập nhật dữ liệu trong adapter
        }

        // Lấy danh sách khóa học ban đầu
        viewModel.getCourseListFromDatabase()
    }

    override fun onResume() {
        super.onResume()
        // Load lại dữ liệu khi người dùng quay lại
        viewModel.getCourseListFromDatabase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
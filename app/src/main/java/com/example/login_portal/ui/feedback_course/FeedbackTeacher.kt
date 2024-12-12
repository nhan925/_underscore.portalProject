package com.example.login_portal.ui.feedback_course

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R

class FeedbackTeacher : Fragment() {
    private lateinit var courseNameTV : TextView
    private lateinit var teacherNameTV : TextView
    private lateinit var guideTV: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var continueButton: Button
    private lateinit var scoreList: MutableList<Int>
    val viewModel = FeedbackCourseViewModel.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feedback_teacher, container, false)
        courseNameTV = view.findViewById(R.id.fb_course_detail_name)
        teacherNameTV = view.findViewById(R.id.fb_course_detail_teacher)
        guideTV = view.findViewById(R.id.fb_course_detail_guide)
        continueButton = view.findViewById(R.id.continueButton)
        recyclerView = view.findViewById(R.id.recyclerView)

        // Nhận dữ liệu từ arguments
        val courseName = arguments?.getString("courseName") ?: ""
        val teacherName = arguments?.getString("teacherName") ?: ""

        courseNameTV.text = courseName
        teacherNameTV.text = teacherName
        guideTV.text = resources.getString(R.string.feedback_teacher_guide)

        val questionList = viewModel.getQuestionList(0, resources)

        recyclerView = view.findViewById(R.id.recyclerView)
        val questionAdapter = AdapterForListQuestion(questionList)
        recyclerView.adapter = questionAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        continueButton.setOnClickListener {
            if (checkIfAllQuestionsAnswered()) {
                scoreList = getScoreList().toMutableList()
                viewModel.updateTeacherScore(scoreList)
                viewModel.updateTotalScore()
                viewModel.postFeedbackCourseAndTeacher { success ->
                    if (success) {
                        Toast.makeText(requireContext(),resources.getString(R.string.feedback_course_success),Toast.LENGTH_SHORT).show()
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), resources.getString(R.string.feedback_course_fail), Toast.LENGTH_SHORT).show()
                    }

                }
            }
            else {
                Toast.makeText(requireContext(), resources.getString(R.string.feedback_course_empty_error), Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    companion object {
        fun newInstance(courseName: String, teacherName: String) = FeedbackTeacher().apply {
            arguments = Bundle().apply {
                putString("courseName", courseName)
                putString("teacherName", teacherName)
            }
        }
    }
    private fun checkIfAllQuestionsAnswered(): Boolean {
        val questionList = (recyclerView.adapter as AdapterForListQuestion).questionList
        return questionList.all { it.isAnswered }
    }

    private fun getScoreList(): List<Int> {
        val questionList = (recyclerView.adapter as AdapterForListQuestion).questionList
        return questionList.map { it.rating }
    }


}
package com.example.login_portal.ui.feedback_course

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.BaseActivity
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentFeedbackCourseBinding
import com.example.login_portal.databinding.FragmentFeedbackCourseListBinding
import com.example.login_portal.ui.feedback_course.AdapterForListCourse
import com.example.login_portal.ui.feedback_course.AdapterForListQuestion
import com.example.login_portal.ui.feedback_course.Course
import com.example.login_portal.ui.feedback_course.FeedbackCourseViewModel
import com.example.login_portal.ui.feedback_course.Question
import com.example.login_portal.utils.Validator


class FeedbackCourseActivity : BaseActivity() {

    private lateinit var courseNameTV : TextView
    private lateinit var teacherNameTV : TextView
    private lateinit var guideTV: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var continueButton: Button
    private lateinit var backButton: Button
    private lateinit var scoreList: MutableList<Int>
    private val viewModel = FeedbackCourseViewModel.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_feedback_course)

        val courseName = intent.getStringExtra("courseName")
        val teacherName = intent.getStringExtra("teacherName")

        courseNameTV = findViewById(R.id.fb_course_detail_name)
        teacherNameTV = findViewById(R.id.fb_course_detail_teacher)
        guideTV = findViewById(R.id.fb_course_detail_guide)
        continueButton = findViewById(R.id.continueButton)
        backButton = findViewById(R.id.backButton)

        courseNameTV.text = courseName
        teacherNameTV.text = teacherName
        guideTV.text = resources.getString(R.string.feedback_course_guide)
        //Dùng ViewModel gọi lấy các câu hỏi
        val questionList = viewModel.getQuestionList(1, resources)

        recyclerView = findViewById(R.id.recyclerView)
        val questionAdapter = AdapterForListQuestion(questionList)
        recyclerView.adapter = questionAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        continueButton.setOnClickListener {
            if (checkIfAllQuestionsAnswered()) {
                //get score
                scoreList = getScoreList().toMutableList()
                viewModel.updateCourseScore(scoreList)

                val feedbackTeacherFragment = FeedbackTeacher.newInstance(courseName ?: "", teacherName ?: "")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fb_course_fragment, feedbackTeacherFragment)
                    .commit()

                backButton.visibility = View.GONE
                continueButton.visibility = View.GONE
            } else {
                // Nếu chưa trả lời hết câu hỏi, yêu cầu người dùng hoàn tất
                Toast.makeText(this, "Vui lòng trả lời tất cả câu hỏi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIfAllQuestionsAnswered(): Boolean {
        val questionList = (recyclerView.adapter as AdapterForListQuestion).questionList
        return questionList.all { it.isAnswered }
    }

    private fun getScoreList(): List<Int> {
        val questionList = (recyclerView.adapter as AdapterForListQuestion).questionList
        return questionList.map { it.rating } // Trả về danh sách các giá trị `rating`
    }

}

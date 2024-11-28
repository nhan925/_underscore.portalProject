package com.example.login_portal.ui.feedback_course

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.BaseActivity
import com.example.login_portal.R



class FeedbackTeacherActivity : BaseActivity() {

    private lateinit var courseNameTV : TextView
    private lateinit var teacherNameTV : TextView
    private lateinit var guideTV: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var continueButton: Button
    private lateinit var backButton: Button



    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_feedback_course)

        val feedbackCourseViewModel = ViewModelProvider(this).get(FeedbackCourseViewModel::class.java)

        val courseName = intent.getStringExtra("courseName")
        val teacherName = intent.getStringExtra("teacherName")

        courseNameTV = findViewById(R.id.fb_course_detail_name)
        teacherNameTV = findViewById(R.id.fb_course_detail_teacher)
        guideTV = findViewById(R.id.fb_course_detail_guide)
        continueButton = findViewById(R.id.continueButton)
        backButton = findViewById(R.id.backButton)
        backButton.visibility = View.GONE

        courseNameTV.text = courseName
        teacherNameTV.text = teacherName
        guideTV.text = resources.getString(R.string.feedback_teacher_guide)
        //Dùng ViewModel gọi lấy các câu hỏi
        val questionList = feedbackCourseViewModel.getQuestionList(0, resources)

        recyclerView = findViewById(R.id.recyclerView)
        val questionAdapter = AdapterForListQuestion(questionList)
        recyclerView.adapter = questionAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        continueButton.setOnClickListener {
            if (checkIfAllQuestionsAnswered()) {
//                val intent = Intent(this, FeedbackTeacherActivity::class.java)
//                intent.putExtra("courseName", courseName)
//                intent.putExtra("teacherName", teacherName)
//                startActivity(intent)
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
}

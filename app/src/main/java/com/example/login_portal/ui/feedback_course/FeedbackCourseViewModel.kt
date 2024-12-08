package com.example.login_portal.ui.feedback_course

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.R

class FeedbackCourseViewModel : ViewModel() {
    private var courseScore: String = ""
    private var teacherScore: String = ""
    private var totalScore: String = ""
    private var classID = ""
    private val _courseList = MutableLiveData<List<Course>>()

    val courseList: LiveData<List<Course>> get() = _courseList

    companion object {
        // Singleton instance of the ViewModel
        @Volatile
        private var instance: FeedbackCourseViewModel? = null

        fun getInstance(): FeedbackCourseViewModel {
            return instance ?: synchronized(this) {
                instance ?: FeedbackCourseViewModel().also { instance = it }
            }
        }
    }

    fun getTitle(resources: Resources, callback: (String) -> Unit) {
        FeedbackCourseDAO.getNewestSemester { newestSemester ->
            val year = newestSemester.year
            val semester = newestSemester.semester.toString()
            val title = resources.getString(R.string.feedback_course_year) + " " +  year + " - " + resources.getString(R.string.feedback_course_semester) + " " + semester
            callback(title)
        }
    }

    fun getCourseListFromDatabase() {
        FeedbackCourseDAO.getCourseFeedback { response ->
            if (response.isNotEmpty()) {
                _courseList.postValue(response)
            } else {
                _courseList.postValue(emptyList())
            }
        }
    }

    fun getQuestionList(key: Int, resources: Resources): List<Question> {
        val questionArray: Array<String>
        if (key == 1) {
            questionArray = resources.getStringArray(R.array.feedback_course_question)
        } else {
            questionArray = resources.getStringArray(R.array.feedback_teacher_question)
        }

        val questionList = mutableListOf<Question>()

        for (questionText in questionArray) {
            questionList.add(Question(questionText, -1))
        }

        return questionList
    }

    fun updateCourseScore(scores: List<Int>) {
        courseScore = "Đánh giá môn học: "
        for (i in scores.indices) {
            val score = scores[i].toString()
            val buffer = "Câu hỏi ${i + 1}: $score"
            if (i == scores.size - 1) {
                courseScore += buffer
            } else {
                courseScore += "$buffer - "
            }
        }
    }

    fun updateTotalScore() {
        totalScore = "$courseScore - $teacherScore"
    }

    fun setClassID(classID: String) {
        this.classID = classID
    }

    fun updateTeacherScore(scores: List<Int>) {
        teacherScore = "Đánh giá giảng viên: "
        for (i in scores.indices) {
            val score = scores[i].toString()
            val buffer = "Câu hỏi ${i + 1}: $score"
            if (i == scores.size - 1) {
                teacherScore += buffer
            } else {
                teacherScore += "$buffer - "
            }
        }
    }

    fun postFeedbackCourseAndTeacher(callback: (Boolean) -> Unit) {
        FeedbackCourseDAO.postCourseFeedback(classID, totalScore) { response ->
            if (response == "Success") {
                callback(true)
            } else {
                callback(false)
            }
        }
    }
}

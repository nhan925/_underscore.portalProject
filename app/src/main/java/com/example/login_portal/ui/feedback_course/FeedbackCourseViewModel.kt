package com.example.login_portal.ui.feedback_course

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.R
import com.example.login_portal.utils.Validator

class FeedbackCourseViewModel : ViewModel() {

    fun getQuestionList(key: Int, resources: Resources): List<Question> {
        val questionArray: Array<String>
        if(key == 1){
            questionArray = resources.getStringArray(R.array.feedback_course_question)
        }
        else{
            questionArray = resources.getStringArray(R.array.feedback_teacher_question)
        }

        val questionList = mutableListOf<Question>()

        for (questionText in questionArray) {
            questionList.add(Question(questionText, -1))
        }

        return questionList
    }
}
package com.example.login_portal.ui.feedback_course

import android.util.Log
import com.example.login_portal.ui.requests.RequestItem
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FeedbackCourseDAO {
    fun getCourseFeedback(callback: (List<Course>) -> Unit) {
        ApiServiceHelper.get("/rpc/get_course_for_feedback") { response ->
            if (response != null) {
                val result: List<Course> = Gson().fromJson(response, object : TypeToken<List<Course>>() {}.type)
                callback(result)
            } else {
                callback(listOf())
            }
        }
    }

    fun getNewestSemester(callback: (NewestSemester) -> Unit) {
        ApiServiceHelper.get("/rpc/get_newest_semester") { response ->
            if (response != null) {
                val result: NewestSemester = Gson().fromJson(response, object : TypeToken<NewestSemester>() {}.type)
                Log.d("FeedbackCourseDAO", "NewestSemester: $result")
                callback(result)
            } else {
                callback(NewestSemester("", 0))
            }
        }
    }

    fun postCourseFeedback(classID: String, totalScore: String, callback: (String) -> Unit) {
        val data = object {
            val v_class_id = classID
            val v_feedback = totalScore
        }

        ApiServiceHelper.post("/rpc/add_feedback_course", data) { response ->
            if(response != null) {
                Log.d("FeedbackCourseDAO", "Response: $response")
                callback(response.toString())
            } else {
                callback("Error")
            }
        }
    }
}
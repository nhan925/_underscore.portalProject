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
                val result: List<Course> = Gson().fromJson(response.string(), object : TypeToken<List<Course>>() {}.type)
                callback(result)
            } else {
                callback(listOf())
            }
        }
    }

    fun postCourseFeedback(classID: String, totalScore: String, callback: (String) -> Unit) {
        val data = object {
            val v_class_id = classID
            val v_feedback = totalScore
        }



        ApiServiceHelper.post("/rpc/add_feedback_course", data) { response ->
            callback(response?.string() ?: "")
        }
    }
}
package com.example.login_portal.ui.admin_manage_course

import android.util.Log
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CourseDAO {
    fun getCourses(callback: (List<Course>?) -> Unit) {
        ApiServiceHelper.get("/Course") { response ->
            if (response != null) {
                try {
                    val courses = Gson().fromJson<List<Course>>(
                        response,
                        object : TypeToken<List<Course>>() {}.type
                    )
                    callback(courses)
                } catch (e: Exception) {
                    Log.e("CourseDAO", "Error parsing courses: ${e.message}")
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }

    fun getMajors(callback: (List<Major>?) -> Unit) {
        ApiServiceHelper.get("/Major") { response ->
            if (response != null) {
                try {
                    val majors = Gson().fromJson<List<Major>>(
                        response,
                        object : TypeToken<List<Major>>() {}.type
                    )
                    callback(majors)
                } catch (e: Exception) {
                    Log.e("CourseDAO", "Error parsing majors: ${e.message}")
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }

    fun manageCourse(request: ManageCourseRequest, callback: (Boolean) -> Unit) {
        ApiServiceHelper.post("/rpc/manage_course", request) { response ->
            callback(response != null)
        }
    }
}
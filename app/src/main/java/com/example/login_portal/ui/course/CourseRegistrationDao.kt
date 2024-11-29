package com.example.login_portal.ui.course

import com.example.login_portal.ui.requests.RequestItem
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CourseRegistrationDao {
    fun getPeriods(callback: (List<Period>) -> Unit) {
        ApiServiceHelper.get("/rpc/get_courses_registration_period") { response ->
            if (response != null) {
                val result: List<Period> = Gson().fromJson(response.string(), object : TypeToken<List<Period>>() {}.type)
                callback(result) // Pass the result to the callback
            } else {
                callback(listOf()) // Pass empty list if no response
            }
        }
    }
}
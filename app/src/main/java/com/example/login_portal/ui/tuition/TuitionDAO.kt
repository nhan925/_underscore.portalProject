package com.example.login_portal.ui.tuition

import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object TuitionDAO {
    fun getListCourseTution(callback: (List<Tuition>) -> Unit) {
        ApiServiceHelper.get("/rpc/get_fee_for_payment") { response ->
            if (response != null) {
                val result: List<Tuition> = Gson().fromJson(response, object : TypeToken<List<Tuition>>() {}.type)
                callback(result)
            } else {
                callback(listOf())
            }
        }
    }
    fun getDetailTuition(year: String, semester: Int, callback: (List<TuitionDetail>) -> Unit) {
        val data = object {
            val v_year = year
            val v_semester_num = semester
        }

        ApiServiceHelper.post("/rpc/get_payment_course_detail", data) { response ->
            if (response != null) {
                val result: List<TuitionDetail> = Gson().fromJson(response, object : TypeToken<List<TuitionDetail>>() {}.type)
                callback(result)
            } else {
                callback(listOf())
            }
        }
    }
}
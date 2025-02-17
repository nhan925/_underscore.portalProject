package com.example.login_portal.ui.course

import android.util.Log
import com.example.login_portal.ui.requests.RequestItem
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CourseRegistrationDao {
    fun getPeriods(callback: (List<Period>) -> Unit) {
        ApiServiceHelper.get("/rpc/get_courses_registration_period") { response ->
            if (response != null) {
                val result: List<Period> = Gson().fromJson(response, object : TypeToken<List<Period>>() {}.type)
                callback(result) // Pass the result to the callback
            } else {
                callback(listOf()) // Pass empty list if no response
            }
        }
    }

    fun getChooseCourseInfo(periodId: Int, callback: (ChooseCourseInformations) -> Unit) {
        val data = object {
            val period_id = periodId
        }

        ApiServiceHelper.post("/rpc/get_choose_courses_information", data) { response ->
            if (response != null) {
                val result: ChooseCourseInformations = Gson().fromJson(response, ChooseCourseInformations::class.java)
                callback(result)
            }
            else {
                callback(ChooseCourseInformations())
            }
        }
    }

    fun getChooseClassInfo(courseId: String, periodId: Int, callback: (ChooseClassesInformations) -> Unit) {
        val data = object {
            val course_id_register = courseId
            val period_id = periodId
        }

        ApiServiceHelper.post("/rpc/get_choose_classes_information", data) { response ->
            if (response != null) {
                val result: ChooseClassesInformations = Gson().fromJson(response, ChooseClassesInformations::class.java)
                callback(result)
            }
            else {
                callback(ChooseClassesInformations())
            }
        }
    }

    fun registerClass(toRegisterClassId: String, registeredClassId: String?, callback: (String) -> Unit) {
        val data = object {
            val to_register_class_id = toRegisterClassId
            val registered_class_id = registeredClassId
        }

        ApiServiceHelper.post("/rpc/register_class", data) { response ->
            if (response != null) {
                callback(response)
            }
            else {
                callback("ERROR")
            }
        }
    }

    fun cancelClass(cancelClassId: String, callback: (String) -> Unit) {
        val data = object {
            val canceled_class_id = cancelClassId
        }

        ApiServiceHelper.post("/rpc/cancel_course", data) { response ->
            if (response != null) {
                callback(response)
            }
            else {
                callback("ERROR")
            }
        }
    }
}
package com.example.login_portal.ui.schedule

import android.util.Log
import com.example.login_portal.ui.information.InformationsForInformation
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.skia.impl.theScope.callback

object SchedulePageDao {
    fun getScheduleOfSemester(year : String,semesterNum : Int,callback: (List<Course>) -> Unit){
        ApiServiceHelper.post("/rpc/get_schedule_page",mapOf("p_year" to year, "p_semester_num" to semesterNum)){ response ->
            if(response != null){
                val gson = Gson()
                val type = object : TypeToken<List<Course>>() {}.type
                val courses: List<Course> = gson.fromJson(response, type)
                Log.e("Course1",courses.toString())
                callback(courses)
            }
            else{
                callback(listOf<Course>())
            }
        }
    }

    fun getSemester(callback: (List<String>) -> Unit){
        ApiServiceHelper.get("/rpc/get_registered_semesters"){response ->
            if(response != null){
                val gson = Gson()
                val type = object : TypeToken<List<String>>() {}.type
                val result: List<String> = gson.fromJson(response, type)
                callback(result)
            }
            else{
                callback(listOf<String>())
            }
        }
    }
}
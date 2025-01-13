package com.example.login_portal.ui.admin_manage_student
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.example.login_portal.ui.admin_manage_student.StudentInfo
import android.util.Log

object UpdateStudentInfoDAO {

    fun fetchAListOfStudents(callback: (List<StudentInfo>) -> Unit) {
        ApiServiceHelper.get("/Student") { response ->
            if (response != null) {
                try {
                    val students = Gson().fromJson(response, Array<StudentInfo>::class.java).toList()
                    Log.d("FetchStudents", "Parsed students: $students")
                    callback(students)
                } catch (e: Exception) {
                    Log.e("FetchStudents", "Error parsing students: ${e.message}")
                    callback(emptyList())
                }
            } else {
                Log.e("FetchStudents", "API returned null response")
                callback(emptyList())
            }
        }
    }

    fun updateStudentInfo(studentId: String, updatedFields: Map<String, Any?>, callback: (Boolean) -> Unit) {
        ApiServiceHelper.post("/rpc/update_student_info", updatedFields) { response ->
            callback(response != null)
        }
    }
}
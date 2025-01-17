package com.example.login_portal.ui.admin_manage_semester
import android.util.Log
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SemesterDAO {
    fun getSemesters(callback: (List<Semester>?) -> Unit) {
        ApiServiceHelper.get("/Semester") { response ->
            if (response != null) {
                try {
                    val semesters = Gson().fromJson<List<Semester>>(
                        response,
                        object : TypeToken<List<Semester>>() {}.type
                    )
                    callback(semesters)
                } catch (e: Exception) {
                    Log.e("SemesterDAO", "Error parsing semesters: ${e.message}")
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }

    fun createSemester(request: CreateSemesterRequest, callback: (Boolean) -> Unit) {
        ApiServiceHelper.post("/rpc/create_new_semester", request) { response ->
            callback(response != null)
        }
    }

    fun updateSemester(request: ManageSemesterRequest, callback: (Boolean) -> Unit) {
        request.copy(operation = "update").let { updateRequest ->
            ApiServiceHelper.post("/rpc/manage_semester", updateRequest) { response ->
                callback(response != null)
            }
        }
    }

    fun deleteSemester(semesterId: Int, callback: (Boolean) -> Unit) {
        val request = ManageSemesterRequest(
            operation = "delete",
            semesterId = semesterId
        )
        ApiServiceHelper.post("/rpc/manage_semester", request) { response ->
            callback(response != null)
        }
    }
}
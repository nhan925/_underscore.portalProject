package com.example.login_portal.ui.scholarship

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.login_portal.ui.course.Period
import com.example.login_portal.utils.ApiServiceHelper
import com.example.login_portal.utils.SupabaseService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ScholarshipDao {
    fun getScholarships(year: String, callback: (List<Scholarship>) -> Unit) {
        val request = object {
            val year = year
        }

        ApiServiceHelper.post("/rpc/get_scholarships_with_status", request) { response ->
            if (response != null) {
                val result: List<Scholarship> = Gson().fromJson(response, object : TypeToken<List<Scholarship>>() {}.type)
                callback(result) // Pass the result to the callback
            } else {
                callback(listOf()) // Pass empty list if no response
            }
        }
    }

    suspend fun uploadFile(context: Context, fileUri: Uri): String? {
        return SupabaseService.uploadFile(context, fileUri)
    }

    fun applyScholarship(id: Int, fileUrl: String, callback: (String) -> Unit) {
        val request = object {
            val id = id
            val file_url = fileUrl
        }

        ApiServiceHelper.post("/rpc/apply_scholarship", request) { response ->
            if (response != null) {
                callback(response)
            } else {
                callback("ERROR")
            }
        }
    }
}
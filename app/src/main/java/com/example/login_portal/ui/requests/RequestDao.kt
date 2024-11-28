package com.example.login_portal.ui.requests

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.login_portal.R
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object RequestDao {
    fun getRequests(keyword: String = "", callback: (List<RequestItem>) -> Unit) {
        val data = object {
            val skip = 0
            val take = Int.MAX_VALUE
            val keyword = keyword
        }

        ApiServiceHelper.post("/rpc/get_all_request_data", data) { response ->
            if (response != null) {
                val result: List<RequestItem> = Gson().fromJson(response.string(), object : TypeToken<List<RequestItem>>() {}.type)
                callback(result) // Pass the result to the callback
            } else {
                callback(listOf()) // Pass empty list if no response
            }
        }
    }

    fun getRequestsStatus(callback: (List<String>) -> Unit) {
        ApiServiceHelper.get("/rpc/get_status_list") { response ->
            if (response != null) {
                val result: List<String> = Gson().fromJson(response.string(), object : TypeToken<List<String>>() {}.type)
                callback(result) // Pass the result to the callback
            } else {
                callback(listOf()) // Pass empty list if no response
            }
        }
    }

    fun getAnswerOfRequest(id: Int, callback: (RequestAnswer) -> Unit) {
        val data = object {
            val req_id = id
        }
        ApiServiceHelper.post("/rpc/get_answer_request", data) { response ->
            if (response != null) {
                val result: RequestAnswer = Gson().fromJson(response.string(), RequestAnswer::class.java)
                if (result.AnswerId == null)
                    callback(RequestAnswer())
                else
                    callback(result)
            }
            else {
                callback(RequestAnswer())
            }
        }
    }

    fun resend(request: RequestItem, context: Context) {
        val data = object {
            val content = request.Content
            val status = context.resources.getString(R.string.request_status_processing)
        }
        ApiServiceHelper.post("/rpc/add_request", data) { response ->
            if (response == null) {
                Toast.makeText(context, context.resources.getString(R.string.request_resend_failed), Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, context.resources.getString(R.string.request_resend_success), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun cancel(requestId: Int, context: Context) {
        val data = object {
            val request_id_update = requestId
            val status_update = context.resources.getString(R.string.request_status_canceled)
        }
        ApiServiceHelper.post("/rpc/update_request_status", data) { response ->
            if (response == null) {
                Toast.makeText(context, context.resources.getString(R.string.request_cancel_failed), Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, context.resources.getString(R.string.request_cancel_success), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
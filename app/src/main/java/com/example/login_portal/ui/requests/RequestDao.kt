package com.example.login_portal.ui.requests

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object RequestDao {
    fun getAllRequest(callback: (List<RequestItem>) -> Unit) {
        val data = object {
            val skip = 0
            val take = 200
            val keyword = ""
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
}
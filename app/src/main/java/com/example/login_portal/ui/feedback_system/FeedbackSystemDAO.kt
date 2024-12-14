package com.example.login_portal.ui.feedback_system

import com.example.login_portal.utils.ApiServiceHelper

object FeedbackSystemDAO {
    fun postFeedback(content: String, type: String, callback: (String) -> Unit) {
        //Post database to feedback
        val data = object {
            val content = content
            val type = type
        }

        ApiServiceHelper.post("/rpc/add_feedback", data) { response ->
            if(response != null) {
                callback(response)
            } else {
                callback("Error")
            }
        }
    }
}
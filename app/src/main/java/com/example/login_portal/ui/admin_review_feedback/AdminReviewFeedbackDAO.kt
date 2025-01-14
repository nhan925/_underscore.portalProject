package com.example.login_portal.ui.admin_review_feedback

import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.example.login_portal.ui.admin_review_feedback.FeedbackApp
import com.google.gson.reflect.TypeToken

object AdminReviewFeedbackDAO {
    fun getFeedbackApp(callback: (List<FeedbackApp>)->Unit){
        ApiServiceHelper.get("/Feedback") { response ->
            response.let {
                val feedBackList : List<FeedbackApp> = Gson().fromJson(response, object : TypeToken<List<FeedbackApp>>() {}.type)
                callback(feedBackList)
            } ?: callback(emptyList())
        }
    }
}
package com.example.login_portal.ui.feedback_system

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.login_portal.utils.Validator

class FeedbackSystemViewModel : ViewModel() {

    private var selectedCategory: String = ""

    private var content: String = ""

    fun updatedSelectedCategory(value: String) {
        selectedCategory = value
    }

    fun updateFeedbackContent(value: String) {
       content = value
    }


    fun postFeedback(callback: (Boolean) -> Unit) {
        FeedbackSystemDAO.postFeedback(content, selectedCategory) { response ->
            if (response == "Error") {
                callback(false)
            } else {
                callback(true)
            }
        }
    }

}
package com.example.login_portal.ui.admin_review_feedback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminReviewFeedbackViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Review Feedback Fragment"
    }
    val text: LiveData<String> = _text
}
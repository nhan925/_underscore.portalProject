package com.example.login_portal.ui.admin_review_feedback

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AdminReviewFeedbackViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdminReviewFeedbackViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AdminReviewFeedbackViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}
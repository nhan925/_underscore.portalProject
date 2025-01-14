package com.example.login_portal.ui.admin_review_feedback

import android.content.Context
import com.example.login_portal.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FeedbackApp (
    var type: String = "",
    val content: String = "",
    var submitted_at: Date = Date(),
    private val context: Context? = null
)
{
    fun getFormattedDate(): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(submitted_at)
    }
}
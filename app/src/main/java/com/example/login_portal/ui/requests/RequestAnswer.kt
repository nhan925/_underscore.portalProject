package com.example.login_portal.ui.requests

import android.content.Context
import com.example.login_portal.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RequestAnswer(
    var AnswerId: Int? = 0,
    var AdminName: String? = "Anonymous",
    var AnswerContent: String? = "...",
    var SubmittedAt: Date? = Date("01/01/1999")
) {
    fun getTimeString(context: Context): String {
        val time = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(SubmittedAt)
        return "${context.getString(R.string.request_detail_answer_by)} $AdminName - $time"
    }
}
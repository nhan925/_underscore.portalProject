package com.example.login_portal.ui.requests

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.login_portal.R
import java.util.Date

data class RequestItem (
    var totalRequests: Int = 0,
    var sequenceNumber: Int = 0,
    var requestId: Int = 0,
    var studentId: String = "",
    var studentName: String = "",
    var content: String = "",
    var status: String = "",
    var submittedAt: Date = Date(),
    var requestName: MutableLiveData<String> = MutableLiveData<String>()
) {
    fun getRequestName(context: Context) {
        val lines = content.split("\r\n")

        val requestReExamiantion = context.resources.getString(R.string.request_reExaminationTitle)
        val requestPhysicalTranscript = context.resources.getString(R.string.request_requestForTranscriptTitle)
        var requestString = ""

        if (lines[0] == requestReExamiantion) {
            requestString = "${lines[0]} ${lines[2].split(": ")[1]} - ${lines[1].replace(": ", " ").trim()}"
        } else if (lines[0] == requestPhysicalTranscript) {
            requestString = "${lines[0]} - TotalTranscript: ${lines[lines.size - 1].split(": ")[1]}"
        }
        requestName.postValue(requestString)
    }
}
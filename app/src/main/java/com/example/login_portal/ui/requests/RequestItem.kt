package com.example.login_portal.ui.requests

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.login_portal.R
import java.util.Date

data class RequestItem (
    var TotalRequests: Int = 0,
    var sequenceNumber: Int = 0,
    var RequestId: Int = 0,
    var StudentId: String = "",
    var StudentName: String = "",
    var Content: String = "",
    var Status: String = "",
    var SubmittedAt: Date = Date(),
) {
    fun getRequestName(context: Context): String {
        val lines = Content.split("\r\n")
        return lines[0]
    }
}
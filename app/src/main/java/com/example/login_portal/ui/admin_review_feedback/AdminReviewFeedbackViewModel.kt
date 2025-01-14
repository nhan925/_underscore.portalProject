package com.example.login_portal.ui.admin_review_feedback

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.login_portal.R

class AdminReviewFeedbackViewModel(private val context: Context) : ViewModel() {
    private val _feedbackAppList = MutableLiveData<List<FeedbackApp>>()
    val feedbackAppList : LiveData<List<FeedbackApp>> get() = _feedbackAppList

    var feedBackList: MutableList<FeedbackApp> = mutableListOf()
    var errorReportList: MutableList<FeedbackApp> = mutableListOf()

    val FEEDBACK = 0
    val ERROR_REPORT = 1

    fun preSolveData(listItem: List<FeedbackApp>){
        feedBackList.clear()
        errorReportList.clear()

        listItem.forEach { item ->
            if (item.type == "Error Report" || item.type == "Báo lỗi") {
                errorReportList.add(item)
            } else if (item.type == "Give feedback" || item.type == "Góp ý") {
                feedBackList.add(item)
            }
        }
        feedBackList.sortByDescending { it.submitted_at }
        errorReportList.sortByDescending { it.submitted_at }
    }

    suspend fun resetAppFeedbackList(type : Int){
        withContext(Dispatchers.IO) {
            AdminReviewFeedbackDAO.getFeedbackApp { data ->
                preSolveData(data)
                if(type == FEEDBACK){
                    _feedbackAppList.postValue(feedBackList)
                }
                else if (type == ERROR_REPORT){
                    _feedbackAppList.postValue(errorReportList)
                }
                Log.e("INFO",_feedbackAppList.value.toString())
            }}
    }


}
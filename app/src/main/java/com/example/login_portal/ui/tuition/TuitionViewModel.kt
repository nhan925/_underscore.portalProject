package com.example.login_portal.ui.tuition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.ui.feedback_course.Course
import com.example.login_portal.ui.feedback_course.FeedbackCourseDAO
import com.example.login_portal.ui.feedback_course.FeedbackCourseViewModel


class TuitionViewModel : ViewModel() {

    private val _tuitionList = MutableLiveData<List<Tuition>>()

    val tuitionList: MutableLiveData<List<Tuition>> get() = _tuitionList

    private val _tuitionDetail = MutableLiveData<List<TuitionDetail>>()
    val tuitionDetail: LiveData<List<TuitionDetail>> get() = _tuitionDetail

    companion object {
        // Singleton instance of the ViewModel
        @Volatile
        private var instance: TuitionViewModel? = null

        fun getInstance(): TuitionViewModel {
            return instance ?: synchronized(this) {
                instance ?: TuitionViewModel().also { instance = it }
            }
        }
    }

    fun getTuitionListFromDatabase() {
        TuitionDAO.getListCourseTution { response ->
            if (response.isNotEmpty()) {
                _tuitionList.postValue(response)
            } else {
                _tuitionList.postValue(emptyList())
            }
        }
    }

    fun getTuitionDetailListFromDatabase(year: String, semester: Int) {
        TuitionDAO.getDetailTuition(year, semester) { response ->
            if (response.isNotEmpty()) {
                _tuitionDetail.postValue(response)
            } else {
                _tuitionDetail.postValue(emptyList())
            }
        }
    }

    fun updatePaymentTuition(amount: Int, callback: (Boolean) -> Unit) {
        TuitionDAO.updatePaymentTuition(amount) { response ->
            if(response) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }
}
package com.example.login_portal.ui.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChooseClassViewModel(var courseId: String, var periodId: Int, var courseName: String, var status: String): ViewModel() {
    private var _info = MutableLiveData<ChooseClassesInformations>()
    val info: LiveData<ChooseClassesInformations> = _info

    fun reset(callback: (Unit?) -> Unit) {
        CourseRegistrationDao.getChooseClassInfo(courseId, periodId) { data ->
            data.PeriodId = periodId
            data.CourseId = courseId
            data.CourseName = courseName
            data.Status = status
            _info.value = data
            callback(null)
        }
    }
}
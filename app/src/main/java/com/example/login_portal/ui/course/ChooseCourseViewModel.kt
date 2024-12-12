package com.example.login_portal.ui.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChooseCourseViewModel(var periodId: Int): ViewModel() {
    private var _info = MutableLiveData<ChooseCourseInformations>()
    val info: LiveData<ChooseCourseInformations> = _info

    fun reset(callback: (Unit?) -> Unit) {
        CourseRegistrationDao.getChooseCourseInfo(periodId) { data ->
            _info.value = data
            callback(null)
        }
    }
}
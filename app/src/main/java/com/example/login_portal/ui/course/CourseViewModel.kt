package com.example.login_portal.ui.course



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.ui.requests.RequestItem

class CourseViewModel : ViewModel() {
    private val _periods = MutableLiveData<List<Period>>()
    val periods: LiveData<List<Period>> = _periods

    init {
        reset()
    }

    fun reset() {
        CourseRegistrationDao.getPeriods { data -> _periods.value = data }
    }
}
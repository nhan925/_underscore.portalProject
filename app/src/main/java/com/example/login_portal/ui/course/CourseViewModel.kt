package com.example.login_portal.ui.course



import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.ui.requests.RequestItem

class CourseViewModel : ViewModel() {
    private val _periods = MutableLiveData<List<Period>>()
    val periods: LiveData<List<Period>> = _periods

    init {
        reset { }
    }

    fun reset(callback: (Unit?) -> Unit) {
        CourseRegistrationDao.getPeriods { data ->
            _periods.value = data
            callback(null)
        }
    }
}
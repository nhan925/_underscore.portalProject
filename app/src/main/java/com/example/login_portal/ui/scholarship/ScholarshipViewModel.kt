package com.example.login_portal.ui.scholarship

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.R
import com.example.login_portal.ui.course.CourseRegistrationDao
import com.example.login_portal.ui.course.Period
import java.text.SimpleDateFormat
import java.util.Locale

class ScholarshipViewModel(val context: Context) : ViewModel() {
    private val _scholarships = MutableLiveData<List<Scholarship>>()
    val scholarships: LiveData<List<Scholarship>> = _scholarships

    val years: List<String>
        get() {
            val _years = mutableListOf<String>()
            _years.add(context.getString(R.string.sholarship_all_years))
            for (scholarship in _scholarships.value!!) {
                val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(scholarship.AnouncementDate)
                if (!_years.contains(year)) {
                    _years.add(year)
                }
            }
            return _years
        }

    init {
        reset("") { }
    }

    fun reset(year: String, callback: (Unit?) -> Unit) {
        ScholarshipDao.getScholarships(year) { data ->
            _scholarships.value = data
            callback(null)
        }
    }
}
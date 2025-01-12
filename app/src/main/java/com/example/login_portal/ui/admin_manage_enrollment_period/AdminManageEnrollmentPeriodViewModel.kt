package com.example.login_portal.ui.admin_manage_enrollment_period

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminManageEnrollmentPeriodViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Enrollment Fragment"
    }
    val text: LiveData<String> = _text
}
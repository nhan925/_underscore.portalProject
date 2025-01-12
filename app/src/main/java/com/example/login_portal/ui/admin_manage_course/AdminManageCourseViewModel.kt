package com.example.login_portal.ui.admin_manage_course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminManageCourseViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Course Fragment"
    }
    val text: LiveData<String> = _text
}
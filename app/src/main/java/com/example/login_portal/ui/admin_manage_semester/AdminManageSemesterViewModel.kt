package com.example.login_portal.ui.admin_manage_semester

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminManageSemesterViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Semester Fragment"
    }
    val text: LiveData<String> = _text
}
package com.example.login_portal.ui.admin_manage_student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminManageStudentViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Admin manage student Fragment"
    }
    val text: LiveData<String> = _text
}
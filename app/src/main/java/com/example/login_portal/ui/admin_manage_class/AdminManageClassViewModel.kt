package com.example.login_portal.ui.admin_manage_class

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminManageClassViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Class Fragment"
    }
    val text: LiveData<String> = _text
}
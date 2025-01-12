package com.example.login_portal.ui.admin_notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminNotificationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Noti Fragment"
    }
    val text: LiveData<String> = _text
}
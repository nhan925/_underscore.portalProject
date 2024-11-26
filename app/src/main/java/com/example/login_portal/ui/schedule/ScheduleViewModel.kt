package com.example.login_portal.ui.schedule


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScheduleViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Dashboard Fragment"
    }
    val text: LiveData<String> = _text
}
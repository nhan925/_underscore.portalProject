package com.example.login_portal.ui.tuition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class TuitionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Dashboard Fragment"
    }
    val text: LiveData<String> = _text
}
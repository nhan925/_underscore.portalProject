package com.example.login_portal.ui.requests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.utils.ApiService

class RequestViewModel : ViewModel() {
    lateinit var text: List<MutableLiveData<RequestItem>>


}
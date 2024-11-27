package com.example.login_portal.ui.requests

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.utils.ApiService

class RequestViewModel : ViewModel() {
    private val _requests = MutableLiveData<List<RequestItem>>()
    val requests: LiveData<List<RequestItem>> = _requests

   init {
        RequestDao.getAllRequest { data ->
            _requests.value = data // Update LiveData, which will trigger UI updates
        }
    }
}
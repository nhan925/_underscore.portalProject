package com.example.login_portal.ui.requests

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RequestViewModel : ViewModel() {
    private val _requests = MutableLiveData<List<RequestItem>>()
    val requests: LiveData<List<RequestItem>> = _requests

    private val _status = MutableLiveData<List<String>>()
    val status: LiveData<List<String>> = _status

    init {
        reset()
    }

    fun filterByStatus(status: String) {
        RequestDao.getRequests(status) { data ->
            _requests.value = data
        }
    }

    fun reset() {
        RequestDao.getRequests { data ->
            _requests.value = data
        }

        RequestDao.getRequestsStatus { data ->
            _status.value = data
        }
    }
}
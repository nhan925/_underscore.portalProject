package com.example.login_portal.ui.requests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RequestDetailAnswerViewModel(val requestId: Int) : ViewModel() {
    private var _answer = MutableLiveData<RequestAnswer>()
    val answer: LiveData<RequestAnswer> = _answer

    init {
        reset()
    }

    fun reset() {
        RequestDao.getAnswerOfRequest(requestId) { data ->
            _answer.value = data
        }
    }
}
package com.example.login_portal.ui.requests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RequestDetailContentViewModel : ViewModel() {
    private var _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private var _time = MutableLiveData<String>()
    val time: LiveData<String> = _time

    private var _content = MutableLiveData<String>()
    val content: LiveData<String> = _content

    fun init(item: RequestItem) {
        _title.value = item.getRequestName()
        _time.value = item.getFormattedDate()
        _content.value = item.Content
    }
}
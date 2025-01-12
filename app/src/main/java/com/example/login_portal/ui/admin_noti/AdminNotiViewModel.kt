package com.example.login_portal.ui.admin_noti

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.login_portal.ui.notification.Notification

class AdminNotiViewModel(application: Application) : AndroidViewModel(application) {

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            AdminNotiDAO.getAdminNotifications { result ->
                if (result != null) {
                    _notifications.postValue(result)
                }
            }
        }
    }
}

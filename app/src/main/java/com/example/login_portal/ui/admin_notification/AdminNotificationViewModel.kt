package com.example.login_portal.ui.admin_notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.login_portal.ui.notification.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminNotiViewModel(application: Application) : AndroidViewModel(application) {

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _createSuccess = MutableLiveData<Boolean>()
    val createSuccess: LiveData<Boolean> get() = _createSuccess

    init {
        fetchNotifications()
    }

    fun fetchNotifications() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            AdminNotiDAO.getAdminNotifications { result ->
                _notifications.postValue(result)
                _isLoading.postValue(false)
            }
        }
    }

    fun createNotification(notificationRequest: AddNotificationRequest) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            AdminNotiDAO.createNotification(notificationRequest) { success ->
                _createSuccess.postValue(success)
                _isLoading.postValue(false)
            }
        }
    }
}

package com.example.login_portal.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login_portal.data.NotificationDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import kotlinx.coroutines.*

class NotificationViewModel : ViewModel() {
    private val notificationDAO = NotificationDAO()

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications

    private val _selectedTab = MutableLiveData<String>()
    val selectedTab: LiveData<String> get() = _selectedTab

    private var pollingJob: Job? = null


    init {
        _selectedTab.value = "All"
        fetchNotifications()
        startPolling()
    }


    private fun fetchNotifications() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val notificationDAO = NotificationDAO()
                notificationDAO.getStudentNotifications { notifications ->
                    if (notifications != null) {
                        for (notification in notifications) {
                            Log.d("TestNotifications", "Title: ${notification.title}, Content: ${notification.detail}, Sender: ${notification.sender}, Time: ${notification.time}, isSeen: ${notification.isSeen}, isImportant: ${notification.isImportant}, isMarkedAsImportant: ${notification.isMarkedAsImportant}")
                            _notifications.postValue(notifications ?: emptyList())
                        }
                    } else {
                        Log.e("TestNotifications", "Failed to fetch notifications")
                    }
                }
            }
        }
    }

    private fun startPolling() {
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(10000L)
                fetchNotifications()
            }
        }
    }

    fun markAsSeen(notificationId: Int) {
        _notifications.value = _notifications.value?.map { notification ->
            if (notification.id == notificationId) notification.copy(isSeen = true) else notification
        }
        viewModelScope.launch {
            notificationDAO.markNotificationAsSeen(notificationId) { success ->
                if (success) {
                    _notifications.value = _notifications.value?.map { notification ->
                        if (notification.id == notificationId) notification.copy(isSeen = true) else notification
                    }
                }
            }
        }
    }

    fun markAsImportant(notificationId: Int) {
        Log.e("NotificationViewModel", "Start to MARK notification $notificationId as important")
        _notifications.value = _notifications.value?.map { notif ->
            if (notif.id == notificationId) notif.copy(isMarkedAsImportant = true) else notif
        }

        viewModelScope.launch {
            notificationDAO.markNotificationAsImportant(notificationId) { success ->
                if (!success) {
                    Log.e("NotificationViewModel", "Failed to mark notification $notificationId as important")
                }
                fetchNotifications() // Đồng bộ lại dữ liệu
            }
        }
    }

    fun deleteNotification(notificationId: Int) {
        _notifications.value = _notifications.value?.filter { it.id != notificationId }
        viewModelScope.launch {
            notificationDAO.deleteNotification(notificationId) { success ->
                if (success) {
                    _notifications.value = _notifications.value?.filter { it.id != notificationId }
                }
            }
        }
    }

    fun unmarkAsImportant(notificationId: Int) {
        Log.e("NotificationViewModel", "Start to unmark notification $notificationId as important")
        _notifications.value = _notifications.value?.map { notif ->
            if (notif.id == notificationId) notif.copy(isMarkedAsImportant = false) else notif
        }

        // Gửi yêu cầu cập nhật đến API
        viewModelScope.launch {
            notificationDAO.unmarkNotificationAsImportant(notificationId) { success ->
                if (!success) {
                    // Nếu API thất bại, log lỗi
                    Log.e("NotificationViewModel", "Failed to unmark notification $notificationId as important")
                }
                // Đồng bộ hóa dữ liệu sau khi API gọi xong
                fetchNotifications()
            }
        }
    }

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }
}

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

class NotificationViewModel : ViewModel() {
    private val notificationDAO = NotificationDAO()

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications

    private val _selectedTab = MutableLiveData<String>()
    val selectedTab: LiveData<String> get() = _selectedTab

    init {
        _selectedTab.value = "All"
        fetchNotifications()
    }


    private fun fetchNotifications() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Fetch and update user info
                val studentInfo = StudentInfo.fetchAndUpdateUserInfo()
                val studentId = studentInfo?.StudentId

                if (studentId.isNullOrEmpty()) {
                    Log.e("NotificationViewModel", "Student ID is null or empty!")
                    _notifications.postValue(emptyList())
                    return@withContext
                }

                Log.d("NotificationViewModel", "Fetching notifications for studentId: $studentId")
                notificationDAO.getStudentNotifications(studentId) { notificationList ->
                    if (notificationList != null) {
                        Log.d("NotificationViewModel", "Fetched ${notificationList.size} notifications.")
                        _notifications.postValue(notificationList ?: emptyList())
                    } else {
                        Log.e("NotificationViewModel", "No notifications fetched.")
                        _notifications.postValue(emptyList())
                    }
                }
            }
        }
    }

    fun markAsSeen(notificationId: Int) {
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
        viewModelScope.launch {
            notificationDAO.markNotificationAsImportant(notificationId) { success ->
                if (success) {
                    _notifications.value = _notifications.value?.map { notification ->
                        if (notification.id == notificationId) notification.copy(isImportant = true, isSeen = true) else notification
                    }
                }
            }
        }
    }

    fun deleteNotification(notificationId: Int) {
        viewModelScope.launch {
            notificationDAO.deleteNotification(notificationId) { success ->
                if (success) {
                    _notifications.value = _notifications.value?.filter { it.id != notificationId }
                }
            }
        }
    }

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }
}

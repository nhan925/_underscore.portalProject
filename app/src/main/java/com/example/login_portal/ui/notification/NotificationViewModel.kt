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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                NotificationDAO.getStudentNotifications { notifications ->
                    notifications?.let {
                        _notifications.postValue(it)
                        Log.d("NotificationViewModel", "Fetched ${it.size} notifications successfully")
                    } ?: run {
                        Log.e("NotificationViewModel", "Failed to fetch notifications: response is null")
                    }
                }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error while fetching notifications: ${e.message}", e)
            }
        }
    }

    private fun startPolling() {
        pollingJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(10000L)
                fetchNotifications()
            }
        }
    }

    fun markAsSeen(notificationId: Int) {
        updateNotificationState(notificationId, isSeen = true)

        viewModelScope.launch(Dispatchers.IO) {
            NotificationDAO.markNotificationAsSeen(notificationId) { success ->
                if (!success) {
                    Log.e("NotificationViewModel", "Failed to mark notification $notificationId as seen")
                }
            }
        }
    }

    fun markAsImportant(notificationId: Int) {
        updateNotificationState(notificationId, isMarkedAsImportant = true)

        viewModelScope.launch(Dispatchers.IO) {
            NotificationDAO.markNotificationAsImportant(notificationId) { success ->
                if (!success) {
                    Log.e("NotificationViewModel", "Failed to mark notification $notificationId as important")
                    revertNotificationState(notificationId, isMarkedAsImportant = false)
                }
            }
        }
    }

    fun unmarkAsImportant(notificationId: Int) {
        updateNotificationState(notificationId, isMarkedAsImportant = false)

        viewModelScope.launch(Dispatchers.IO) {
            NotificationDAO.unmarkNotificationAsImportant(notificationId) { success ->
                if (!success) {
                    Log.e("NotificationViewModel", "Failed to unmark notification $notificationId as important")
                    revertNotificationState(notificationId, isMarkedAsImportant = true)
                }
            }
        }
    }

    fun deleteNotification(notificationId: Int) {
        removeNotification(notificationId)

        viewModelScope.launch(Dispatchers.IO) {
            NotificationDAO.deleteNotification(notificationId) { success ->
                if (!success) {
                    Log.e("NotificationViewModel", "Failed to delete notification $notificationId")
                }
            }
        }
    }

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }

    private fun updateNotificationState(notificationId: Int, isSeen: Boolean? = null, isMarkedAsImportant: Boolean? = null) {
        _notifications.value = _notifications.value?.map { notification ->
            if (notification.id == notificationId) {
                notification.copy(
                    isSeen = isSeen ?: notification.isSeen,
                    isMarkedAsImportant = isMarkedAsImportant ?: notification.isMarkedAsImportant
                )
            } else {
                notification
            }
        }
    }

    private fun revertNotificationState(notificationId: Int, isSeen: Boolean? = null, isMarkedAsImportant: Boolean? = null) {
        _notifications.value = _notifications.value?.map { notification ->
            if (notification.id == notificationId) {
                notification.copy(
                    isSeen = isSeen ?: notification.isSeen,
                    isMarkedAsImportant = isMarkedAsImportant ?: notification.isMarkedAsImportant
                )
            } else {
                notification
            }
        }
    }

    private fun removeNotification(notificationId: Int) {
        _notifications.value = _notifications.value?.filter { it.id != notificationId }
    }
}

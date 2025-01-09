package com.example.login_portal.ui.notification

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.login_portal.data.NotificationDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications
    private val notifiedNotifications = mutableSetOf<Int>()

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
                    val fetchedNotifications = notifications ?: emptyList()

                    // Filter new and unseen notifications only
                    val newNotification = fetchedNotifications.firstOrNull { notification ->
                        !notification.isSeen && notification.id !in notifiedNotifications
                    }

                    if (newNotification != null) {
                        Log.d("NotificationUtils", "New notification detected: ${newNotification.title}")
                        detectNewNotification(newNotification)
                        notifiedNotifications.add(newNotification.id)
                    } else {
                        Log.d("NotificationUtils", "No new notifications detected")
                    }

                    // Update the notification list but skip showing read notifications
                    _notifications.postValue(fetchedNotifications)
                }
            } catch (e: Exception) {
                Log.e("NotificationUtils", "Error while fetching notifications: ${e.message}", e)
            }
        }
    }

    private fun startPolling() {
        pollingJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(5000L) // Poll every 10 seconds
                fetchNotifications() // Fetch notifications
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

    fun detectNewNotification(newNotification: Notification) {
        Log.d("NotificationUtils", "Displaying system notification for: ${newNotification.id} ${newNotification.title}")
        NotificationUtils.showSystemNotification(
            context,
            newNotification.title,
            newNotification.detail
        )
    }
}

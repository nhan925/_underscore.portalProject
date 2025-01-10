package com.example.login_portal.ui.notification

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.example.login_portal.data.NotificationDAO

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("NotificationWorker", "Worker is running to fetch notifications...")

        try {
            NotificationDAO.getStudentNotifications { notifications ->
                notifications?.let {
                    val unseenNotifications = it.filter { notification ->
                        !notification.isSeen && !isNotificationDisplayed(notification.id)
                    }

                    unseenNotifications.forEach { notification ->
                        Log.d("NotificationWorker", "New notification: ${notification.title}")
                        saveDisplayedNotification(notification.id)
                        NotificationUtils.showSystemNotification(
                            applicationContext,
                            notification.title,
                            notification.detail,
                            notification.id,
                            notification.detail
                        )
                    }
                } ?: Log.e("NotificationWorker", "No notifications found")
            }
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error fetching notifications: ${e.message}", e)
            return Result.retry()
        }

        // Schedule the next fetch
        scheduleNextFetch()
        return Result.success()
    }

    private fun scheduleNextFetch() {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)
        Log.d("NotificationWorker", "Scheduled next fetch in 5 seconds")
    }

    // SharedPreferences-based implementation to track displayed notifications
    private fun saveDisplayedNotification(notificationId: Int) {
        val sharedPref = applicationContext.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        val displayedSet = sharedPref.getStringSet("displayed_notifications", mutableSetOf()) ?: mutableSetOf()
        displayedSet.add(notificationId.toString())
        sharedPref.edit().putStringSet("displayed_notifications", displayedSet).apply()
    }

    private fun isNotificationDisplayed(notificationId: Int): Boolean {
        val sharedPref = applicationContext.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        val displayedSet = sharedPref.getStringSet("displayed_notifications", mutableSetOf()) ?: mutableSetOf()
        return displayedSet.contains(notificationId.toString())
    }
}

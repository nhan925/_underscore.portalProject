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

    companion object {
        private val displayedNotifications = mutableSetOf<Int>() // Để theo dõi các thông báo đã hiển thị
    }

    override fun doWork(): Result {
        Log.d("NotificationWorker", "Worker is running to fetch notifications...")
        try {
            NotificationDAO.getStudentNotifications { notifications ->
                notifications?.let {
                    val unseenNotifications = it.filter { notification ->
                        !notification.isSeen && !displayedNotifications.contains(notification.id)
                    }

                    unseenNotifications.forEach { notification ->
                        Log.d("NotificationWorker", "New notification: ${notification.title}")
                        displayedNotifications.add(notification.id) // Đánh dấu thông báo đã hiển thị
                        NotificationUtils.showSystemNotification(
                            applicationContext,
                            notification.title,
                            notification.detail
                        )
                    }
                } ?: Log.e("NotificationWorker", "No notifications found")
            }
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error fetching notifications: ${e.message}", e)
            return Result.retry()
        }

        // Lên lịch fetch tiếp theo
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
}

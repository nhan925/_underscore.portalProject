package com.example.login_portal.data

import android.util.Log
import com.example.login_portal.ui.notification.Notification
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NotificationDAO {

    fun getStudentNotifications(callback: (List<Notification>?) -> Unit) {
        Log.d("NotificationDAO", "Fetching notifications using GET request")

        ApiServiceHelper.get("/rpc/get_student_notifications") { response ->
            if (response != null) {
                try {
                    val gson = Gson()
                    val type = object : TypeToken<List<Notification>>() {}.type
                    val notifications: List<Notification> = gson.fromJson(response, type)

                    Log.d("NotificationDAO", "Fetched ${notifications.size} notifications successfully.")
                    callback(notifications)
                } catch (e: Exception) {
                    Log.e("NotificationDAO", "Error parsing response: ${e.message}", e)
                    callback(null)
                }
            } else {
                Log.e("NotificationDAO", "Failed to fetch notifications. Response is null.")
                callback(null)
            }
        }
    }

    fun markNotificationAsSeen(notificationId: Int, callback: (Boolean) -> Unit) {
        Log.d("NotificationDAO", "Marking notification as seen: $notificationId")

        ApiServiceHelper.post(
            "/rpc/update_notification_status",
            mapOf("notification_id_input" to notificationId.toString(), "action" to "mark_seen")
        ) { response ->
            if (response != null) {
                Log.d("NotificationDAO", "Notification $notificationId marked as seen successfully.")
                callback(true)
            } else {
                Log.e("NotificationDAO", "Failed to mark notification $notificationId as seen.")
                callback(false)
            }
        }
    }

    fun markNotificationAsImportant(notificationId: Int, callback: (Boolean) -> Unit) {
        Log.d("NotificationDAO", "Marking notification as important: $notificationId")

        ApiServiceHelper.post(
            "/rpc/update_notification_status",
            mapOf("notification_id_input" to notificationId.toString(), "action" to "mark_important")
        ) { response ->
            if (response != null) {
                Log.d("NotificationDAO", "Notification $notificationId marked as important successfully.")
                callback(true)
            } else {
                Log.e("NotificationDAO", "Failed to mark notification $notificationId as important.")
                callback(false)
            }
        }
    }

    fun unmarkNotificationAsImportant(notificationId: Int, callback: (Boolean) -> Unit) {
        Log.d("NotificationDAO", "Unmarking notification as important: $notificationId")

        ApiServiceHelper.post(
            "/rpc/update_notification_status",
            mapOf("notification_id_input" to notificationId.toString(), "action" to "mark_unimportant")
        ) { response ->
            if (response != null) {
                Log.d("NotificationDAO", "Notification $notificationId unmarked as important successfully.")
                callback(true)
            } else {
                Log.e("NotificationDAO", "Failed to unmark notification $notificationId as important.")
                callback(false)
            }
        }
    }

    fun deleteNotification(notificationId: Int, callback: (Boolean) -> Unit) {
        Log.d("NotificationDAO", "Deleting notification: $notificationId")

        ApiServiceHelper.post(
            "/rpc/update_notification_status",
            mapOf("notification_id_input" to notificationId.toString(), "action" to "delete")
        ) { response ->
            if (response != null) {
                Log.d("NotificationDAO", "Notification $notificationId deleted successfully.")
                callback(true)
            } else {
                Log.e("NotificationDAO", "Failed to delete notification $notificationId.")
                callback(false)
            }
        }
    }
}

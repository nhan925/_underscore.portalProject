package com.example.login_portal.ui.admin_notification

import android.util.Log
import com.example.login_portal.ui.notification.Notification
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AdminNotiDAO {

    fun getAdminNotifications(callback: (List<Notification>?) -> Unit) {
        ApiServiceHelper.get("/Notification") { response ->
            if (response != null) {
                try {
                    val notifications = Gson().fromJson<List<Notification>>(
                        response,
                        object : TypeToken<List<Notification>>() {}.type
                    )
                    callback(notifications)
                } catch (e: Exception) {
                    Log.e("AdminNotiDAO", "Error parsing notifications: ${e.message}")
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }

    fun createNotification(notificationRequest: AddNotificationRequest, callback: (Boolean) -> Unit) {
        val requestBody = mapOf(
            "is_important" to notificationRequest.isImportant,
            "notification_message" to notificationRequest.message,
            "notification_title" to notificationRequest.title
        )
        ApiServiceHelper.post("/rpc/add_notification", requestBody) { response ->
            if (response != null) {
                callback(true)
            } else {
                Log.e("AdminNoti", "Failed to receive a response from the server")
                callback(false)
            }
        }
    }
}

package com.example.login_portal.data

import android.util.Log
import com.example.login_portal.ui.notification.Notification
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NotificationDAO {

    fun getStudentNotifications(studentId: String, callback: (List<Notification>?) -> Unit) {
        // Log the API request
        Log.d("NotificationDAO", "Fetching notifications for studentId: $studentId")

        // API Call
        ApiServiceHelper.post(
            "/rpc/get_student_notifications",
            mapOf("student_id_input" to studentId)
        ) { response ->
            if (response != null) {
                try {
                    // Parse JSON response to Notification objects
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
}

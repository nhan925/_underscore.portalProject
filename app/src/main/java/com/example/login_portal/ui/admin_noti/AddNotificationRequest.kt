package com.example.login_portal.ui.admin_noti
import com.google.gson.annotations.SerializedName

data class AddNotificationRequest(
    @SerializedName("notification_title") val title: String,
    @SerializedName("notification_message") val message: String,
    @SerializedName("is_important") val isImportant: Boolean
)
package com.example.login_portal.ui.notification

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("notification_id") val id: Int,
    @SerializedName("notification_title") val title: String,
    @SerializedName("admin_sender") val sender: String,
    @SerializedName("notification_message") val detail: String,
    @SerializedName("is_important") val isImportant: Boolean,
    @SerializedName("created_at") val time: String,
    @SerializedName("read_status") var isSeen: Boolean,
    @SerializedName("is_marked_as_important") var isMarkedAsImportant: Boolean // If needed
)
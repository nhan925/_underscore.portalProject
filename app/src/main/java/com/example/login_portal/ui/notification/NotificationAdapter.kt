package com.example.login_portal.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R

class NotificationAdapter(
    private var notifications: List<Notification>,
    private val onNotificationClicked: (Notification) -> Unit // Callback for click actions
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_notification_title)
        val sender: TextView = view.findViewById(R.id.tv_notification_sender)
        val time: TextView = view.findViewById(R.id.tv_notification_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]



        // Set data
        holder.title.text = notification.title
        holder.sender.text = notification.sender
        holder.time.text = notification.time

        // Apply bold style for unseen notifications
        holder.title.setTypeface(
            null,
            if (!notification.isSeen) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL
        )

        // Set click listener to open the detailed notification screen
        holder.itemView.setOnClickListener {
            onNotificationClicked(notification)
        }
    }

    override fun getItemCount(): Int = notifications.size

    // Update the notifications list and notify the adapter
    fun updateNotifications(newNotifications: List<Notification>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }

    fun getNotificationAtPosition(position: Int): Notification {
        return notifications[position]
    }

    fun removeNotificationAtPosition(position: Int) {
        val mutableList = notifications.toMutableList()
        mutableList.removeAt(position)
        notifications = mutableList
        notifyItemRemoved(position)
    }

    fun getNotifications(): List<Notification> {
        return notifications
    }
}
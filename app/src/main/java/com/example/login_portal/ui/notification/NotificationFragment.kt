package com.example.login_portal.ui.notification

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentNotificationBinding

const val REQUEST_NOTIFICATION_DETAIL = 1

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private val notificationViewModel: NotificationViewModel by activityViewModels()
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.rvNotifications
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = NotificationAdapter(mutableListOf()) { notification ->
            val intent = Intent(requireContext(), NotificationDetailActivity::class.java).apply {
                putExtra("notification_id", notification.id)
                putExtra("notification_title", notification.title)
                putExtra("notification_sender", notification.sender)
                putExtra("notification_time", notification.time)
                putExtra("notification_detail", notification.detail)
            }
            startActivityForResult(intent, REQUEST_NOTIFICATION_DETAIL)
        }
        recyclerView.adapter = adapter

        notificationViewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            updateFilteredNotifications()
        }

        notificationViewModel.selectedTab.observe(viewLifecycleOwner) {
            updateFilteredNotifications()
        }

        // Initialize tabs
        val tabAll = binding.tabAll
        val tabImportant = binding.tabImportant
        val tabUnread = binding.tabUnread

        setSelectedTab(tabAll, tabImportant, tabUnread)
        notificationViewModel.setSelectedTab("All")

        tabAll.setOnClickListener {
            setSelectedTab(tabAll, tabImportant, tabUnread)
            notificationViewModel.setSelectedTab("All")
        }

        tabImportant.setOnClickListener {
            setSelectedTab(tabImportant, tabAll, tabUnread)
            notificationViewModel.setSelectedTab("Important")
        }

        tabUnread.setOnClickListener {
            setSelectedTab(tabUnread, tabAll, tabImportant)
            notificationViewModel.setSelectedTab("Unread")
        }

        return root
    }

    private fun setSelectedTab(selected: TextView, vararg others: TextView) {
        selected.setBackgroundResource(R.drawable.tab_selected_bg)
        selected.setTextColor(Color.WHITE)

        for (tab in others) {
            tab.setBackgroundResource(R.drawable.tab_unselected_bg)
            tab.setTextColor(Color.BLACK)
        }
    }

    private fun updateFilteredNotifications() {
        val notifications = notificationViewModel.notifications.value ?: emptyList()
        val selectedTab = notificationViewModel.selectedTab.value ?: "All"
        val filteredNotifications = when (selectedTab) {
            "All" -> notifications
            "Important" -> notifications.filter { it.isImportant }
            "Unread" -> notifications.filter { !it.isSeen }
            else -> emptyList()
        }
        adapter.updateNotifications(filteredNotifications)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val notificationId = data.getIntExtra("notification_id", -1)
            val action = data.getStringExtra("action") ?: return

            when (action) {
                "mark_important" -> notificationViewModel.markAsImportant(notificationId)
                "delete" -> notificationViewModel.deleteNotification(notificationId)
                "mark_seen" -> notificationViewModel.markAsSeen(notificationId)
            }
        }
    }
}

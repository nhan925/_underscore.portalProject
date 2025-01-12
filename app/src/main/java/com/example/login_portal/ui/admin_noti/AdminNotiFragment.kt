package com.example.login_portal.ui.admin_noti

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentAdminNotificationBinding
import com.example.login_portal.ui.notification.Notification


class AdminNotiFragment : Fragment() {

    private var _binding: FragmentAdminNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var adminNotiViewModel: AdminNotiViewModel
    private lateinit var adapter: AdminNotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminNotificationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        adminNotiViewModel = ViewModelProvider(this).get(AdminNotiViewModel::class.java)

        binding.rvNotifications.layoutManager = LinearLayoutManager(context)
        adapter = AdminNotificationAdapter(mutableListOf()) { notification ->

        }
        binding.rvNotifications.adapter = adapter

        adminNotiViewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            adapter.updateNotifications(notifications)
        }

        binding.addNotiFAB.setOnClickListener {
            val intent = Intent(context, AdminCreateNotiActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
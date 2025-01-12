package com.example.login_portal.ui.admin_notification

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_portal.databinding.FragmentAdminNotificationBinding

class AdminNotificationFragment : Fragment() {

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
        adminNotiViewModel = ViewModelProvider(this).get(AdminNotiViewModel::class.java)

        setupRecyclerView()
        setupObservers()

        binding.addNotiFAB.setOnClickListener {
            startActivity(Intent(requireContext(), AdminCreateNotiActivity::class.java))
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = AdminNotificationAdapter(emptyList()) { notification ->
            val intent = Intent(requireContext(), AdminNotificationDetailActivity::class.java).apply {
                putExtra("NOTIFICATION_TITLE", notification.title)
                putExtra("NOTIFICATION_CONTENT", notification.detail)
                putExtra("NOTIFICATION_TIME", notification.time)
            }
            startActivity(intent)
        }
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifications.adapter = adapter
    }

    private fun setupObservers() {
        adminNotiViewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            adapter.updateNotifications(notifications ?: emptyList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

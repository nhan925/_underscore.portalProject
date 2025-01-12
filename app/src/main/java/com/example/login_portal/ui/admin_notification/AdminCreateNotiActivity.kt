package com.example.login_portal.ui.admin_notification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.login_portal.R
import com.example.login_portal.databinding.ActivityAdminCreateNotiBinding

class AdminCreateNotiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminCreateNotiBinding
    private val adminNotiViewModel: AdminNotiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminCreateNotiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        adminNotiViewModel.isLoading.observe(this) { isLoading ->
            binding.shimmerView.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnCreate.isEnabled = !isLoading
        }

        adminNotiViewModel.createSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Notification created successfully.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to create notification.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnCreate.setOnClickListener {
            val title = binding.etNotificationTitle.text.toString().trim()
            val content = binding.etContent.text.toString().trim()
            val isImportant = binding.cbImportant.isChecked

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Title and content cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val notificationRequest = AddNotificationRequest(title, content, isImportant)
            adminNotiViewModel.createNotification(notificationRequest)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}

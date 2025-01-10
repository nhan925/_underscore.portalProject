package com.example.login_portal.ui.notification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.BaseActivity
import com.example.login_portal.R
import com.example.login_portal.ui.notification.NotificationViewModel

class NotificationDetailActivity : BaseActivity() {
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var tvNotificationTitle: TextView
    private lateinit var tvSender: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvContent: TextView
    private lateinit var tvHeader: TextView
    private lateinit var btnImportant: Button
    private lateinit var btnDelete: Button
    private lateinit var btnBack: ImageView
    private var notificationId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_detail)

        // Initialize views using findViewById
        tvNotificationTitle = findViewById(R.id.tvNotificationTitle)
        tvSender = findViewById(R.id.tvSender)
        tvTime = findViewById(R.id.tvTime)
        tvContent = findViewById(R.id.tvContent)
        tvHeader = findViewById(R.id.tvHeader)
        btnImportant = findViewById(R.id.btnImportant)
        btnDelete = findViewById(R.id.btnDelete)
        btnBack = findViewById(R.id.btnBack)

        notificationId = intent.getIntExtra("notification_id", -1)
        notificationViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)

        // Extract data from Intent
        val title = intent.getStringExtra("notification_title") ?: getString(R.string.notification_title)
        val sender = intent.getStringExtra("notification_sender") ?: getString(R.string.notification_sender)
        val time = intent.getStringExtra("notification_time") ?: getString(R.string.notification_time)
        val detail = intent.getStringExtra("notification_detail") ?: getString(R.string.notification_content)
        val isMarkedAsImportant = intent.getBooleanExtra("is_marked_as_important", false) // Lấy giá trị

        // Set data to UI
        tvNotificationTitle.text = title
        tvSender.text = sender
        tvTime.text = time
        tvContent.text = detail
        tvHeader.text = getString(R.string.notification)

        if (notificationId != -1) {
            notificationViewModel.markAsSeen(notificationId)
        } else {
            finish()
        }

        // Set button text based on the importance
        btnImportant.setText(
            if (isMarkedAsImportant) R.string.unmark_as_important else R.string.mark_as_important
        )

        btnImportant.setOnClickListener {
            val action = if (isMarkedAsImportant) "unmark_important" else "mark_important"

            if (notificationId != -1) {
                if (isMarkedAsImportant) {
                    notificationViewModel.unmarkAsImportant(notificationId)
                } else {
                    notificationViewModel.markAsImportant(notificationId)
                }
            }

            val resultIntent = Intent().apply {
                putExtra("notification_id", notificationId)
                putExtra("action", action)
            }
            setResult(Activity.RESULT_OK, resultIntent)

            val message = if (isMarkedAsImportant) {
                getString(R.string.notification_removed_from_important)
            } else {
                getString(R.string.notification_added_to_important)
            }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            finish()
        }

        btnDelete.setOnClickListener {
            if (notificationId != -1) {
                notificationViewModel.deleteNotification(notificationId)
            }

            val resultIntent = Intent().apply {
                putExtra("notification_id", notificationId)
                putExtra("action", "delete")
            }
            setResult(Activity.RESULT_OK, resultIntent)
            Toast.makeText(this, getString(R.string.notification_delete_message), Toast.LENGTH_SHORT).show()
            finish()
        }

        btnBack.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("notification_id", notificationId)
                putExtra("action", "mark_seen")
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}

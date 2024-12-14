package com.example.login_portal.ui.notification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login_portal.R

class NotificationDetailActivity : AppCompatActivity() {

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

        // Extract data from Intent
        val title = intent.getStringExtra("notification_title") ?: getString(R.string.notification_title)
        val sender = intent.getStringExtra("notification_sender") ?: getString(R.string.notification_sender)
        val time = intent.getStringExtra("notification_time") ?: getString(R.string.notification_time)
        val detail = intent.getStringExtra("notification_detail") ?: getString(R.string.notification_content)

        // Set data to UI
        tvNotificationTitle.text = title
        tvSender.text = getString(R.string.notification_sender)
        tvTime.text = getString(R.string.notification_time)
        tvContent.text = detail
        tvHeader.text = getString(R.string.notification)

        // Set localized text for buttons
        btnImportant.text = getString(R.string.mark_as_important)
        btnDelete.text = getString(R.string.notification_delete)

        // Button actions
        btnImportant.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("notification_id", notificationId)
                putExtra("action", "mark_important")
            }
            setResult(Activity.RESULT_OK, resultIntent)
            Toast.makeText(this, getString(R.string.mark_as_important), Toast.LENGTH_SHORT).show()
            finish()
        }

        btnDelete.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("notification_id", notificationId)
                putExtra("action", "delete")
            }
            setResult(Activity.RESULT_OK, resultIntent)
            Toast.makeText(this, getString(R.string.notification_delete), Toast.LENGTH_SHORT).show()
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

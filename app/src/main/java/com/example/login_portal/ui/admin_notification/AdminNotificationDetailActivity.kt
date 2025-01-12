package com.example.login_portal.ui.admin_notification

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.login_portal.R
import android.widget.ImageView
import com.example.login_portal.BaseActivity


class AdminNotificationDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_see_noti)
        var btnBack = findViewById<ImageView>(R.id.btnBack)

        val title = intent.getStringExtra("NOTIFICATION_TITLE")
        val content = intent.getStringExtra("NOTIFICATION_CONTENT")
        val time = intent.getStringExtra("NOTIFICATION_TIME")

        findViewById<TextView>(R.id.tvNotificationTitle).text = title
        findViewById<TextView>(R.id.tvNotificationContent).text = content
        findViewById<TextView>(R.id.tvNotificationTime).text = time

        btnBack.setOnClickListener {
            finish()
        }
    }
}

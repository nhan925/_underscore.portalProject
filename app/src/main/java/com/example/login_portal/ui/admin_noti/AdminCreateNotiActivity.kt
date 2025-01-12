package com.example.login_portal.ui.admin_noti

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login_portal.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.button.MaterialButton

class AdminCreateNotiActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var cbImportant: CheckBox
    private lateinit var btnCreate: MaterialButton
    private lateinit var shimmerView: ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_create_noti)

        etTitle = findViewById(R.id.etNotificationTitle)
        etContent = findViewById(R.id.etContent)
        cbImportant = findViewById(R.id.cbImportant)
        btnCreate = findViewById(R.id.btnCreate)
        shimmerView = findViewById(R.id.shimmer_view)

        btnCreate.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()
            val isImportant = cbImportant.isChecked

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Title and content cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val notificationRequest = AddNotificationRequest(
                title = title,
                message = content,
                isImportant = isImportant
            )

            showLoading()

            AdminNotiDAO.createNotification(notificationRequest) { success ->
                runOnUiThread {
                    hideLoading()

                    if (success) {
                        Toast.makeText(this, "Notification created successfully.", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity
                    } else {
                        Toast.makeText(this, "Failed to create notification. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        shimmerView.visibility = View.VISIBLE
        shimmerView.startShimmer()
        btnCreate.isEnabled = false
    }

    private fun hideLoading() {
        shimmerView.stopShimmer()
        shimmerView.visibility = View.GONE
        btnCreate.isEnabled = true
    }
}

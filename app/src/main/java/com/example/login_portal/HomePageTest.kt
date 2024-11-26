package com.example.login_portal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.login_portal.utils.SecurePrefManager

class HomePageTest : BaseActivity() {
    private lateinit var sercurePrefManager: SecurePrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page_test)
        sercurePrefManager = SecurePrefManager(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            logout()
        }

    }

    private fun logout() {
        // Xóa thông tin đăng nhập đã lưu
        sercurePrefManager.clearUserData()

        // Chuyển về màn hình đăng nhập
        val intent = Intent(this, MainActivity2::class.java)
        // Xóa tất cả activity trong stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Đóng activity hiện tại
        finish()
    }
}
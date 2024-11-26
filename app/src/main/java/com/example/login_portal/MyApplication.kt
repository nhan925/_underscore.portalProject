package com.example.login_portal

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

class MyApplication : Application() {
    companion object {
        private lateinit var instance: MyApplication
        fun getInstance(): MyApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Khởi tạo ngôn ngữ khi app khởi động
        val savedLanguage = getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
            .getString("appLanguage", "en") ?: "en"
        updateLocale(savedLanguage)
    }

    // Hàm này lấy
    fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)

        val context = createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Cập nhật context cho toàn bộ ứng dụng
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
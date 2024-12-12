package com.example.login_portal

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.login_portal.utils.ApiServiceHelper
import java.util.Locale

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val languageCode = newBase.getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
            .getString("appLanguage", "en") ?: "en"

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Đảm bảo cập nhật ngôn ngữ khi Activity được tạo
        val languageCode = getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
            .getString("appLanguage", "en") ?: "en"
        updateConfiguration(languageCode)
    }

    private fun updateConfiguration(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)

        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
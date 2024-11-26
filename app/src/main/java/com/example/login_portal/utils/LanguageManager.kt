package com.example.login_portal.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.widget.ImageView
import android.widget.TextView
import com.example.login_portal.MyApplication
import com.example.login_portal.R
import java.util.Locale

class LanguageManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("appPreferences", Context.MODE_PRIVATE)

    fun getCurrentLanguage(): String {
        return sharedPreferences.getString("appLanguage", "en") ?: "en"
    }

    fun toggleLanguage(languageFlag: ImageView, languageText: TextView): String {
        val currentLanguage = getCurrentLanguage()
        val newLanguage = if (currentLanguage == "en") "vi" else "en"

        // Lưu ngôn ngữ mới
        with(sharedPreferences.edit()) {
            putString("appLanguage", newLanguage)
            apply()
        }

        // Cập nhật ngôn ngữ ở cấp Application
        MyApplication.getInstance().updateLocale(newLanguage)
        updateLanguageUI(languageFlag, languageText, newLanguage)

        // Yêu cầu Activity hiện tại recreate để áp dụng thay đổi
        if (context is Activity) {
            context.recreate()
        }

        return newLanguage
    }

//    fun updateLocale(languageCode: String) {
//        val locale = Locale(languageCode)
//        Locale.setDefault(locale)
//        val config = Configuration(context.resources.configuration)
//        config.setLocale(locale)
//        context.resources.updateConfiguration(config, context.resources.displayMetrics)
//    }

    fun updateLanguageUI(flagImageView: ImageView, textView: TextView, language: String) {
        if (language == "en") {
            flagImageView.setImageResource(R.drawable.english_flag)
            textView.text = context.getString(R.string.language_eng)
        } else {
            flagImageView.setImageResource(R.drawable.vietnam_flag)
            textView.text = context.getString(R.string.language_vie)
        }
    }
}
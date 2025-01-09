package com.example.login_portal

import AppLifecycleListener
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import androidx.work.PeriodicWorkRequestBuilder
import com.example.login_portal.ui.notification.NotificationWorker
import java.util.concurrent.TimeUnit
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingWorkPolicy
import androidx.lifecycle.ProcessLifecycleOwner
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent


class MyApplication : Application() {
    companion object {
        private lateinit var instance: MyApplication
        fun getInstance(): MyApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener())
        // Khởi tạo ngôn ngữ khi app khởi động
        val savedLanguage = getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
            .getString("appLanguage", "en") ?: "en"
        updateLocale(savedLanguage)
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onAppBackgrounded() {
                Log.d("MyApplication", "App transitioned to background")
                scheduleNotificationFetch()
            }
        })

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

    private fun scheduleNotificationFetch() {
        Log.d("NotificationWorker", "Start fetching notifications...")
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "NotificationFetch",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.login_portal.ui.notification.NotificationWorker
import java.util.concurrent.TimeUnit

class AppLifecycleListener(private val context: Context) : LifecycleObserver {

    companion object {
        var isAppInForeground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        isAppInForeground = true
        Log.d("AppLifecycleListener", "App entered foreground")
        // Cancel background notification fetch when app is in foreground
        WorkManager.getInstance(context).cancelUniqueWork("NotificationFetch")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        isAppInForeground = false
        Log.d("AppLifecycleListener", "App entered background")
        Log.d("AppLifecycleListener", "App is: $isAppInForeground")

        scheduleNotificationFetch()
    }

    private fun scheduleNotificationFetch() {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS) // Delay before the worker starts
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "NotificationFetch",
            ExistingWorkPolicy.REPLACE, // Replace any existing work with the same name
            workRequest
        )

        Log.d("AppLifecycleListener", "Scheduled notification fetch in background")
    }
}

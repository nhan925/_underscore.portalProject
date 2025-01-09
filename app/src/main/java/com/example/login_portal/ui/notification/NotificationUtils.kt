import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.login_portal.R
import java.util.*
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build.VERSION_CODES.TIRAMISU



object NotificationUtils {
    private const val CHANNEL_ID = "new_notifications_channel"
    private const val CHANNEL_NAME = "New Notifications"

    // Hiển thị thông báo ngoài ứng dụng
    fun showSystemNotification(
        context: Context,
        title: String,
        message: String
    ) {
        Log.d("NotificationUtils", "Showing system notification: $title - $message")
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("NotificationUtils", "Notification channel IS BEING created")
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            Log.d("NotificationUtils", "Notification channel created: $channel")
        }


        Log.d("NotificationUtils", "Started to build notification")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .build()
        Log.d("NotificationUtils", "Notification pop up NOW")
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        Log.d("NotificationUtils", "Notification pop up OK")
    }
}

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.login_portal.R
import com.example.login_portal.ui.notification.NotificationDetailActivity

object NotificationUtils {
    private const val CHANNEL_ID = "new_notifications_channel"
    private const val CHANNEL_NAME = "New Notifications"

    fun showSystemNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int,
        notificationDetail: String,
        sender: String,
        time: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Intent mở NotificationDetailActivity mà không làm mất ngăn xếp ứng dụng chính
        val intent = Intent(context, NotificationDetailActivity::class.java).apply {
            putExtra("notification_id", notificationId)
            putExtra("notification_title", title)
            putExtra("notification_detail", notificationDetail)
            putExtra("notification_sender", sender)
            putExtra("notification_time", time)
            putExtra("is_marked_as_important", false)

        }

        // PendingIntent với FLAG_ACTIVITY_SINGLE_TOP để giữ nguyên ngăn xếp ứng dụng
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Xây dựng thông báo
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Tự động xóa thông báo sau khi nhấn
            .setContentIntent(pendingIntent)
            .build()

        // Hiển thị thông báo
        notificationManager.notify(notificationId, notification)
    }
}

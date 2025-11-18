package com.ondot.platform.util

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dh.core.platform.R
import com.ondot.domain.model.request.local_notification.LocalNotificationRequest
import com.ondot.domain.service.LocalNotificationScheduler
import kotlin.getValue

class AndroidLocalNotificationScheduler(
    private val context: Context
): LocalNotificationScheduler {

    private val notificationManager by lazy {
        NotificationManagerCompat.from(context)
    }

    private val alarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun schedule(request: LocalNotificationRequest) {
        ensureChannel()

        val pendingIntent = createPendingIntent(request)

        val triggerAt = request.triggerAtMillis

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntent
        )
    }

    override fun cancel(id: String) {
        val dummy = LocalNotificationRequest(
            id = id,
            title = "",
            body = "",
            triggerAtMillis = System.currentTimeMillis()
        )
        val pendingIntent = createPendingIntent(dummy)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

        // 이미 표시된 노티도 제거 (NotificationManager)
        val notificationId = id.hashCode()
        notificationManager.cancel(notificationId)
    }

    private fun createPendingIntent(request: LocalNotificationRequest): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_SHOW_LOCAL_NOTIFICATION
            putExtra(EXTRA_ID, request.id)
            putExtra(EXTRA_TITLE, request.title)
            putExtra(EXTRA_BODY, request.body)
        }

        val requestCode = request.id.hashCode()

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun ensureChannel() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val existing = manager.getNotificationChannel(CHANNEL_ID)
        if (existing != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Alarms & Schedules",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Local notifications for schedules/alarms"
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
        }

        manager.createNotificationChannel(channel)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    internal fun showNow(id: String, title: String, body: String) {
        ensureChannel()

        val notificationId = id.hashCode()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val CHANNEL_ID = "local_notifications"

        const val ACTION_SHOW_LOCAL_NOTIFICATION =
            "com.yourapp.SHOW_LOCAL_NOTIFICATION"

        const val EXTRA_ID = "extra_notification_id"
        const val EXTRA_TITLE = "extra_notification_title"
        const val EXTRA_BODY = "extra_notification_body"
    }
}
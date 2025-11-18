package com.ondot.platform.util

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission

class NotificationReceiver: BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != AndroidLocalNotificationScheduler.ACTION_SHOW_LOCAL_NOTIFICATION) return

        val id = intent.getStringExtra(AndroidLocalNotificationScheduler.EXTRA_ID) ?: return
        val title = intent.getStringExtra(AndroidLocalNotificationScheduler.EXTRA_TITLE).orEmpty()
        val body = intent.getStringExtra(AndroidLocalNotificationScheduler.EXTRA_BODY).orEmpty()

        // 알림 표시
        AndroidLocalNotificationScheduler(context).showNow(
            id = id,
            title = title,
            body = body
        )
    }
}
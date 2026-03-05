package com.ondot.platform.util

import com.ondot.domain.model.request.localNotification.LocalNotificationRequest
import com.ondot.domain.service.LocalNotificationScheduler
import platform.Foundation.NSDate
import platform.Foundation.NSLog
import platform.Foundation.timeIntervalSince1970
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

// import platform.UserNotifications.*

class IosLocalNotificationScheduler : LocalNotificationScheduler {
    override fun schedule(request: LocalNotificationRequest) {
        val center = UNUserNotificationCenter.currentNotificationCenter()

        val content =
            UNMutableNotificationContent().apply {
                setTitle(request.title)
                setBody(request.body)
            }

        val now = NSDate()
        val targetSeconds = request.triggerAtMillis.toDouble() / 1000.0
        val nowSeconds = now.timeIntervalSince1970
        val interval = maxOf(1.0, targetSeconds - nowSeconds)

        val trigger =
            UNTimeIntervalNotificationTrigger
                .triggerWithTimeInterval(interval, repeats = false)

        val req =
            UNNotificationRequest.requestWithIdentifier(
                identifier = request.id,
                content = content,
                trigger = trigger,
            )

        center.addNotificationRequest(req) { error ->
            error?.let {
                NSLog("Local notification schedule error: %@", it.localizedDescription)
            }
        }
    }

    override fun cancel(id: String) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removePendingNotificationRequestsWithIdentifiers(listOf(id))
        center.removeDeliveredNotificationsWithIdentifiers(listOf(id))
    }
}

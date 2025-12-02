package com.ondot.testing.fake.util

import com.ondot.domain.model.request.local_notification.LocalNotificationRequest
import com.ondot.domain.service.LocalNotificationScheduler

class FakeNotificationScheduler: LocalNotificationScheduler {
    val scheduled = mutableListOf<LocalNotificationRequest>()
    val cancelled = mutableListOf<String>()

    override fun schedule(request: LocalNotificationRequest) {
        scheduled += request
    }

    override fun cancel(id: String) {
        cancelled += id
    }
}
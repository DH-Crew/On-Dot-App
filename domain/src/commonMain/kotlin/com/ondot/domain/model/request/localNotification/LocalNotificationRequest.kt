package com.ondot.domain.model.request.localNotification

data class LocalNotificationRequest(
    val id: String,
    val title: String,
    val body: String,
    val triggerAtMillis: Long, // epoch millis
)

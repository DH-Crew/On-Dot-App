package com.ondot.domain.model.request.local_notification

data class LocalNotificationRequest(
    val id: String,
    val title: String,
    val body: String,
    val triggerAtMillis: Long    // epoch millis
)
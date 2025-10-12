package com.ondot.domain.model.request

data class ScheduleAlarmRequest(
    val appointmentAt: String,
    val startLongitude: Double,
    val startLatitude: Double,
    val endLongitude: Double,
    val endLatitude: Double
)

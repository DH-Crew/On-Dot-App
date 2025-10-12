package com.dh.ondot.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleAlarmRequest(
    val appointmentAt: String,
    val startLongitude: Double,
    val startLatitude: Double,
    val endLongitude: Double,
    val endLatitude: Double
)

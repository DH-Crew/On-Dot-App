package com.ondot.data.model.response.calendar

import kotlinx.serialization.Serializable

@Serializable
data class RecordScheduleResponse(
    val scheduleId: Long,
    val scheduleTitle: String,
    val isRepeat: Boolean,
    val repeatDays: List<Int>,
    val appointmentAt: String,
    val preparationTriggeredAt: String,
    val departureTriggeredAt: String,
    val startLongitude: Double,
    val startLatitude: Double,
    val endLongitude: Double,
    val endLatitude: Double,
)

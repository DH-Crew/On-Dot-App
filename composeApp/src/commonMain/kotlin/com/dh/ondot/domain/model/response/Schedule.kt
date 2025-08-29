package com.dh.ondot.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val scheduleId: Long,
    val startLongitude: Double,
    val startLatitude: Double,
    val endLongitude: Double,
    val endLatitude: Double,
    val scheduleTitle: String,
    val isRepeat: Boolean,
    val repeatDays: List<Int>,
    val appointmentAt: String,
    val preparationAlarm: AlarmDetail,
    val departureAlarm: AlarmDetail,
    val hasActiveAlarm: Boolean
)

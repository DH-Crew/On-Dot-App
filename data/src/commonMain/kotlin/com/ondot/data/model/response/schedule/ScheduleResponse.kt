package com.ondot.data.model.response.schedule

import com.ondot.data.model.response.alarm.AlarmResponse
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleResponse(
    val scheduleId: Long = -1L,
    val startLongitude: Double = 0.0,
    val startLatitude: Double = 0.0,
    val endLongitude: Double = 0.0,
    val endLatitude: Double = 0.0,
    val scheduleTitle: String = "",
    val isRepeat: Boolean = false,
    val repeatDays: List<Int> = emptyList(),
    val appointmentAt: String = "",
    val preparationAlarm: AlarmResponse = AlarmResponse(),
    val departureAlarm: AlarmResponse = AlarmResponse(),
    val hasActiveAlarm: Boolean = false,
    val preparationNote: String = ""
)

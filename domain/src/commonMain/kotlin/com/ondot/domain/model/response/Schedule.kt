package com.ondot.domain.model.response

data class Schedule(
    val scheduleId: Long = -1L,
    val startLongitude: Double = 0.0,
    val startLatitude: Double = 0.0,
    val endLongitude: Double = 0.0,
    val endLatitude: Double = 0.0,
    val scheduleTitle: String = "",
    val isRepeat: Boolean = false,
    val repeatDays: List<Int> = emptyList(),
    val appointmentAt: String = "",
    val preparationAlarm: AlarmDetail = AlarmDetail(),
    val departureAlarm: AlarmDetail = AlarmDetail(),
    val hasActiveAlarm: Boolean = false
)

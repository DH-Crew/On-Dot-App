package com.ondot.domain.model.calendar

data class RecordSchedule(
    val scheduleId: Long = -1L,
    val scheduleTitle: String = "",
    val isRepeat: Boolean = false,
    val repeatDays: List<Int> = emptyList(),
    val appointmentAt: String = "",
    val preparationTriggeredAt: String = "",
    val departureTriggeredAt: String = "",
    val startLongitude: Double = 0.0,
    val startLatitude: Double = 0.0,
    val endLongitude: Double = 0.0,
    val endLatitude: Double = 0.0,
)

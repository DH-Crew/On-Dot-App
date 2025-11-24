package com.ondot.domain.model.ui

import com.ondot.domain.model.alarm.Alarm
import com.ondot.domain.model.enums.AlarmType

data class AlarmRingInfo(
    val alarm: Alarm = Alarm(),
    val alarmType: AlarmType = AlarmType.Departure,
    val appointmentAt: String = "",
    val scheduleTitle: String = "",
    val scheduleId: Long = -1L,
    val startLat: Double = 0.0,
    val startLng: Double = 0.0,
    val endLat: Double = 0.0,
    val endLng: Double = 0.0,
    val repeatDays: List<Int> = emptyList()
)

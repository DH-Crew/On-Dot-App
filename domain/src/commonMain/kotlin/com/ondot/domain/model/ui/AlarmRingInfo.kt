package com.ondot.domain.model.ui

import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.response.AlarmDetail

data class AlarmRingInfo(
    val alarmDetail: AlarmDetail = AlarmDetail(),
    val alarmType: AlarmType = AlarmType.Departure,
    val appointmentAt: String = "",
    val scheduleTitle: String = "",
    val scheduleId: Long = -1L,
    val startLat: Double = 0.0,
    val startLng: Double = 0.0,
    val endLat: Double = 0.0,
    val endLng: Double = 0.0,
)

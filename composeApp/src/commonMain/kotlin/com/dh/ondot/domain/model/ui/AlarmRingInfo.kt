package com.dh.ondot.domain.model.ui

import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.model.response.AlarmDetail
import kotlinx.serialization.Serializable

@Serializable
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

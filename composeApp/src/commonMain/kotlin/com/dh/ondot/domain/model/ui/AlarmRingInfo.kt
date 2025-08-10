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
    val startLng: Double = 127.0276,
    val startLat: Double = 37.4979,
    val endLat: Double = 37.501,
    val endLng: Double = 127.029,
)

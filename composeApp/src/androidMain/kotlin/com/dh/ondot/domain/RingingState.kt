package com.dh.ondot.domain

import com.ondot.domain.model.enums.AlarmType
import kotlinx.serialization.Serializable

@Serializable
data class RingingState(
    val isRinging: Boolean = false,
    val scheduleId: Long = -1,
    val alarmId: Long = -1,
    val type: AlarmType = AlarmType.Departure
)

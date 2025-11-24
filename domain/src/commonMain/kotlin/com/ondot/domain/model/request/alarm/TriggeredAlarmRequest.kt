package com.ondot.domain.model.request.alarm

import com.ondot.domain.model.enums.AlarmAction
import kotlinx.serialization.Serializable

@Serializable
data class TriggeredAlarmRequest(
    val scheduleId: Long,
    val alarmId: Long,
    val action: AlarmAction
)

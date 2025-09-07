package com.dh.ondot.domain.model.ui

import com.dh.ondot.domain.model.enums.AlarmType

data class AlarmEvent(
    val scheduleId: Long,
    val alarmId: Long,
    val type: AlarmType
)

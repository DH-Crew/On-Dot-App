package com.dh.ondot.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleAlarmResponse(
    val preparationAlarm: AlarmDetail = AlarmDetail(),
    val departureAlarm: AlarmDetail = AlarmDetail()
)

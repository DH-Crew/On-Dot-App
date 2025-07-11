package com.dh.ondot.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleAlarmResponse(
    val preparationAlarm: AlarmDetail,
    val departureAlarm: AlarmDetail
)

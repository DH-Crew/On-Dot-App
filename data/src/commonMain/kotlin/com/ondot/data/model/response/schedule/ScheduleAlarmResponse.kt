package com.ondot.data.model.response.schedule

import com.ondot.data.model.response.alarm.AlarmResponse
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleAlarmResponse(
    val preparationAlarm: AlarmResponse = AlarmResponse(),
    val departureAlarm: AlarmResponse = AlarmResponse()
)

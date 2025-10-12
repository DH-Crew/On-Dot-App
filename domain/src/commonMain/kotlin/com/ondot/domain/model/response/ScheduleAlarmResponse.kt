package com.ondot.domain.model.response

data class ScheduleAlarmResponse(
    val preparationAlarm: AlarmDetail = AlarmDetail(),
    val departureAlarm: AlarmDetail = AlarmDetail()
)

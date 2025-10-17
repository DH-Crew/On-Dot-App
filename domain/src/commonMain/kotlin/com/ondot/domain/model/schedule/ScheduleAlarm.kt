package com.ondot.domain.model.schedule

import com.ondot.domain.model.alarm.Alarm


data class ScheduleAlarm(
    val preparationAlarm: Alarm = Alarm(),
    val departureAlarm: Alarm = Alarm()
)
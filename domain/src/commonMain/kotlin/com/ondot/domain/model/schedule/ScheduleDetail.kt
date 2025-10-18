package com.ondot.domain.model.schedule

import com.ondot.domain.model.alarm.Alarm
import com.ondot.domain.model.member.AddressInfo
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDetail(
    val title: String = "",
    val isRepeat: Boolean = false,
    val repeatDays: List<Int> = emptyList(),
    val appointmentAt: String = "",
    val departurePlace: AddressInfo = AddressInfo(),
    val arrivalPlace: AddressInfo = AddressInfo(),
    val preparationAlarm: Alarm = Alarm(),
    val departureAlarm: Alarm = Alarm()
)
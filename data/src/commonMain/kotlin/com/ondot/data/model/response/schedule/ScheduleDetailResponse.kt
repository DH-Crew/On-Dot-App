package com.ondot.data.model.response.schedule

import com.ondot.data.model.response.alarm.AlarmResponse
import com.ondot.data.model.response.member.AddressResponse
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDetailResponse(
    val title: String = "",
    val isRepeat: Boolean = false,
    val repeatDays: List<Int> = emptyList(),
    val appointmentAt: String = "",
    val departurePlace: AddressResponse = AddressResponse(),
    val arrivalPlace: AddressResponse = AddressResponse(),
    val preparationAlarm: AlarmResponse = AlarmResponse(),
    val departureAlarm: AlarmResponse = AlarmResponse()
)

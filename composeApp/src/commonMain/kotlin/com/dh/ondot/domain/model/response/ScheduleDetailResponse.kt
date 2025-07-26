package com.dh.ondot.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDetailResponse(
    val title: String = "",
    val isRepeat: Boolean = false,
    val repeatDays: List<Int> = emptyList(),
    val appointmentAt: String = "",
    val departurePlace: AddressInfo = AddressInfo(),
    val arrivalPlace: AddressInfo = AddressInfo(),
    val preparationAlarm: AlarmDetail = AlarmDetail(),
    val departureAlarm: AlarmDetail = AlarmDetail()
)

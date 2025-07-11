package com.dh.ondot.domain.model.request

import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.domain.model.response.AlarmDetail

data class CreateScheduleRequest(
    val title: String,
    val isRepeat: Boolean,
    val repeatDays: List<Int>,
    val appointmentAt: String,
    val departurePlace: AddressInfo,
    val arrivalPlace: AddressInfo,
    val preparationAlarm: AlarmDetail,
    val departureAlarm: AlarmDetail
)

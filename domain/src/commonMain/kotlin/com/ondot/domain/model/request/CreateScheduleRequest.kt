package com.ondot.domain.model.request

import com.ondot.domain.model.alarm.Alarm
import com.ondot.domain.model.member.AddressInfo
import kotlinx.serialization.Serializable

@Serializable
data class CreateScheduleRequest(
    val title: String,
    val isRepeat: Boolean,
    val repeatDays: List<Int>,
    val appointmentAt: String,
    val isMedicationRequired: Boolean,
    val preparationNote: String,
    val departurePlace: AddressInfo,
    val arrivalPlace: AddressInfo,
    val preparationAlarm: Alarm,
    val departureAlarm: Alarm
)

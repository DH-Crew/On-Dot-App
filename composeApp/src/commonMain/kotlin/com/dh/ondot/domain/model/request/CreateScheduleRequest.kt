package com.dh.ondot.domain.model.request

import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.domain.model.response.AlarmDetail
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
    val preparationAlarm: AlarmDetail,
    val departureAlarm: AlarmDetail
)

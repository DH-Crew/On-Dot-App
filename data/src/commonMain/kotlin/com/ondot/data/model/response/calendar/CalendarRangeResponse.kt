package com.ondot.data.model.response.calendar

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalendarRangeResponse(
    @SerialName("days")
    val days: List<CalendarDayResponse>,
)

@Serializable
data class CalendarDayResponse(
    @SerialName("date")
    val date: String,
    @SerialName("schedules")
    val schedules: List<CalendarScheduleSummaryResponse>,
)

@Serializable
data class CalendarScheduleSummaryResponse(
    @SerialName("scheduleId")
    val scheduleId: Long,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: String,
    @SerialName("isRepeat")
    val isRepeat: Boolean,
    @SerialName("appointmentAt")
    val appointmentAt: String,
)

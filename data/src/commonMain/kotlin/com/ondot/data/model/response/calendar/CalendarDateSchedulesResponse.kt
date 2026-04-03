package com.ondot.data.model.response.calendar

import com.ondot.data.model.response.schedule.ScheduleResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalendarDateSchedulesResponse(
    @SerialName("schedules")
    val schedules: List<ScheduleResponse>,
)

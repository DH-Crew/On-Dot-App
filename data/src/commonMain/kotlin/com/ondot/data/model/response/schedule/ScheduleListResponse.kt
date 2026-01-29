package com.ondot.data.model.response.schedule

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleListResponse(
    val earliestAlarmId: Long?,
    val earliestAlarmAt: String?,
    val hasNext: Boolean?,
    val scheduleList: List<ScheduleResponse>?
)

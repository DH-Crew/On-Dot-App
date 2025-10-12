package com.dh.ondot.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleListResponse(
    val earliestAlarmAt: String?,
    val hasNext: Boolean,
    val scheduleList: List<Schedule>
)

package com.ondot.domain.model.response

data class ScheduleListResponse(
    val earliestAlarmAt: String?,
    val hasNext: Boolean,
    val scheduleList: List<Schedule>
)

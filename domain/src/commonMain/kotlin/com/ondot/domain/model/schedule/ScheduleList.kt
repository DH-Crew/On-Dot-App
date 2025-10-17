package com.ondot.domain.model.schedule

import com.ondot.domain.model.schedule.Schedule

data class ScheduleList(
    val earliestAlarmAt: String = "",
    val hasNext: Boolean = false,
    val scheduleList: List<Schedule> = emptyList()
)

package com.ondot.domain.model.calendar

import com.ondot.domain.model.schedule.Schedule

data class CalendarDateSchedules(
    val records: List<RecordSchedule> = emptyList(),
    val schedules: List<Schedule> = emptyList(),
)

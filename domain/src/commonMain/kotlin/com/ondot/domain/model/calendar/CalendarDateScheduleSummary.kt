package com.ondot.domain.model.calendar

import kotlinx.datetime.LocalDate

data class CalendarDateScheduleSummary(
    val date: LocalDate,
    val schedules: List<CalendarDateScheduleSummaryItem>,
)

data class CalendarDateScheduleSummaryItem(
    val scheduleId: Long,
    val title: String,
)

package com.ondot.domain.model.calendar

import kotlinx.datetime.LocalDate

data class CalendarDateScheduleSummary(
    val date: LocalDate,
    val titles: List<String>,
)

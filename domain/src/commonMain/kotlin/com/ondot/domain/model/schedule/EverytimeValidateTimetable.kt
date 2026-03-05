package com.ondot.domain.model.schedule

import com.ondot.domain.model.enums.DayOfWeekKey

data class EverytimeValidateTimetable(
    val timetable: Map<DayOfWeekKey, List<TimetableEntry>>,
)

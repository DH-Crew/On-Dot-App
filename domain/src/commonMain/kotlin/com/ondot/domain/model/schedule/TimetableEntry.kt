package com.ondot.domain.model.schedule

import kotlinx.datetime.LocalTime

data class TimetableEntry(
    val courseName: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
)

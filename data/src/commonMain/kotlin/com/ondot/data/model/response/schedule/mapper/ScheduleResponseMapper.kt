package com.ondot.data.model.response.schedule.mapper

import com.ondot.data.model.response.schedule.EverytimeValidateResponse
import com.ondot.data.model.response.schedule.TimetableEntryResponse
import com.ondot.domain.model.enums.DayOfWeekKey
import com.ondot.domain.model.schedule.EverytimeValidateTimetable
import com.ondot.domain.model.schedule.TimetableEntry

fun EverytimeValidateResponse.toDomain(): EverytimeValidateTimetable {
    val mapped: Map<DayOfWeekKey, List<TimetableEntry>> =
        timetable
            .mapNotNull { (key, entries) ->
                val day = key.toDayOfWeekKeyOrNull() ?: return@mapNotNull null
                day to entries.map { it.toDomain() }
            }.toMap()

    return EverytimeValidateTimetable(timetable = mapped)
}

fun TimetableEntryResponse.toDomain(): TimetableEntry =
    TimetableEntry(
        courseName = courseName,
        startTime = startTime,
        endTime = endTime,
    )

private fun String.toDayOfWeekKeyOrNull(): DayOfWeekKey? = runCatching { DayOfWeekKey.valueOf(this) }.getOrNull()

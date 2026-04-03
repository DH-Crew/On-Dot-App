package com.ondot.data.model.response.schedule.mapper

import com.ondot.data.model.response.alarm.mapper.toDomain
import com.ondot.data.model.response.schedule.EverytimeValidateResponse
import com.ondot.data.model.response.schedule.ScheduleResponse
import com.ondot.data.model.response.schedule.TimetableEntryResponse
import com.ondot.domain.model.enums.DayOfWeekKey
import com.ondot.domain.model.enums.ScheduleType
import com.ondot.domain.model.schedule.EverytimeValidateTimetable
import com.ondot.domain.model.schedule.Schedule
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

fun ScheduleResponse.toDomain(): Schedule =
    Schedule(
        scheduleId = scheduleId,
        type = if (type != null) ScheduleType.valueOf(type) else ScheduleType.UNKNOWN,
        scheduleTitle = scheduleTitle,
        isRepeat = isRepeat,
        repeatDays = repeatDays,
        appointmentAt = appointmentAt,
        preparationAlarm = preparationAlarm.toDomain(),
        departureAlarm = departureAlarm.toDomain(),
        hasActiveAlarm = hasActiveAlarm,
        startLongitude = startLongitude,
        startLatitude = startLatitude,
        endLongitude = endLongitude,
        endLatitude = endLatitude,
        preparationNote = preparationNote.orEmpty(),
    )

private fun String.toDayOfWeekKeyOrNull(): DayOfWeekKey? = runCatching { DayOfWeekKey.valueOf(this) }.getOrNull()

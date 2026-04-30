package com.ondot.data.model.response.calendar.mapper

import com.ondot.data.model.response.calendar.CalendarDateSchedulesResponse
import com.ondot.data.model.response.calendar.CalendarRangeResponse
import com.ondot.data.model.response.calendar.RecordScheduleResponse
import com.ondot.data.model.response.schedule.mapper.toDomain
import com.ondot.domain.model.calendar.CalendarDateScheduleSummary
import com.ondot.domain.model.calendar.CalendarDateScheduleSummaryItem
import com.ondot.domain.model.calendar.CalendarDateSchedules
import com.ondot.domain.model.calendar.RecordSchedule
import kotlinx.datetime.LocalDate

fun CalendarRangeResponse.toDomain(): List<CalendarDateScheduleSummary> =
    days.map { day ->
        CalendarDateScheduleSummary(
            date = LocalDate.parse(day.date),
            schedules =
                day.schedules.map {
                    CalendarDateScheduleSummaryItem(
                        scheduleId = it.scheduleId,
                        title = it.title,
                    )
                },
        )
    }

fun CalendarDateSchedulesResponse.toDomain(): CalendarDateSchedules =
    CalendarDateSchedules(
        records = records.map { it.toDomain() },
        schedules = schedules.map { it.toDomain() },
    )

fun RecordScheduleResponse.toDomain(): RecordSchedule =
    RecordSchedule(
        scheduleId = scheduleId,
        scheduleTitle = scheduleTitle,
        isRepeat = isRepeat,
        repeatDays = repeatDays,
        appointmentAt = appointmentAt,
        preparationTriggeredAt = preparationTriggeredAt,
        departureTriggeredAt = departureTriggeredAt,
        startLongitude = startLongitude,
        startLatitude = startLatitude,
        endLongitude = endLongitude,
        endLatitude = endLatitude,
    )

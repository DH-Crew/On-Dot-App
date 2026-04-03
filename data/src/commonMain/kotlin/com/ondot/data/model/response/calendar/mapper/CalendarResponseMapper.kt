package com.ondot.data.model.response.calendar.mapper

import com.ondot.data.model.response.calendar.CalendarDateSchedulesResponse
import com.ondot.data.model.response.calendar.CalendarRangeResponse
import com.ondot.data.model.response.schedule.mapper.toDomain
import com.ondot.domain.model.calendar.CalendarDateScheduleSummary
import com.ondot.domain.model.schedule.Schedule
import kotlinx.datetime.LocalDate

fun CalendarRangeResponse.toDomain(): List<CalendarDateScheduleSummary> =
    days.map { day ->
        CalendarDateScheduleSummary(
            date = LocalDate.parse(day.date),
            titles = day.schedules.map { it.title },
        )
    }

fun CalendarDateSchedulesResponse.toDomain(): List<Schedule> = schedules.map { it.toDomain() }

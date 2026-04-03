package com.ondot.domain.repository

import com.ondot.domain.model.calendar.CalendarDateScheduleSummary
import com.ondot.domain.model.schedule.Schedule
import com.ondot.result.AppResult

interface CalendarRepository {
    suspend fun getScheduleMarkersInRange(
        startDate: String,
        endDate: String,
    ): AppResult<List<CalendarDateScheduleSummary>>

    suspend fun getSchedulesFor(date: String): AppResult<List<Schedule>> // 특정 날짜 일정 조회
}

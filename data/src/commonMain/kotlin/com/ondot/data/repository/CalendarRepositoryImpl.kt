package com.ondot.data.repository

import com.ondot.data.model.response.calendar.CalendarDateSchedulesResponse
import com.ondot.data.model.response.calendar.CalendarRangeResponse
import com.ondot.data.model.response.calendar.mapper.toDomain
import com.ondot.domain.model.calendar.CalendarDateScheduleSummary
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.repository.CalendarRepository
import com.ondot.network.HttpMethod
import com.ondot.network.NetworkClient
import com.ondot.network.execute.safeApiCall
import com.ondot.result.AppResult

class CalendarRepositoryImpl(
    private val networkClient: NetworkClient,
) : CalendarRepository {
    override suspend fun getScheduleMarkersInRange(
        startDate: String,
        endDate: String,
    ): AppResult<List<CalendarDateScheduleSummary>> =
        safeApiCall {
            networkClient
                .requestOrThrow<CalendarRangeResponse>(
                    method = HttpMethod.GET,
                    path = "/calendar?startDate=$startDate&endDate=$endDate",
                ).toDomain()
        }

    override suspend fun getSchedulesFor(date: String): AppResult<List<Schedule>> =
        safeApiCall {
            networkClient
                .requestOrThrow<CalendarDateSchedulesResponse>(
                    method = HttpMethod.GET,
                    path = "/calendar/$date",
                ).toDomain()
        }

    override suspend fun deleteHistory(
        scheduleId: Long,
        date: String,
    ): AppResult<Unit> =
        safeApiCall {
            networkClient
                .requestOrThrow<Unit>(
                    method = HttpMethod.DELETE,
                    path = "/calendar/records",
                    queryParams =
                        mapOf(
                            "scheduleId" to "$scheduleId",
                            "date" to date,
                        ),
                )
        }
}

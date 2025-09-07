package com.dh.ondot.data.repository

import com.dh.ondot.core.network.BaseRepository
import com.dh.ondot.core.network.HttpMethod
import com.dh.ondot.core.network.NetworkClient
import com.dh.ondot.domain.datasource.ScheduleLocalDataSource
import com.dh.ondot.domain.model.request.CreateScheduleRequest
import com.dh.ondot.domain.model.request.ScheduleAlarmRequest
import com.dh.ondot.domain.model.request.ToggleAlarmRequest
import com.dh.ondot.domain.model.response.Schedule
import com.dh.ondot.domain.model.response.ScheduleAlarmResponse
import com.dh.ondot.domain.model.response.ScheduleDetail
import com.dh.ondot.domain.model.response.ScheduleListResponse
import com.dh.ondot.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ScheduleRepositoryImpl(
    networkClient: NetworkClient,
    private val local: ScheduleLocalDataSource
) : ScheduleRepository, BaseRepository(networkClient) {

    override suspend fun getScheduleList(): Flow<Result<ScheduleListResponse>> = flow {
        val remote = fetch<ScheduleListResponse>(HttpMethod.GET, "/schedules")

        emit(remote)

        remote.onSuccess {
            local.upsertAll(it.scheduleList)
        }
    }

    override suspend fun getScheduleAlarms(request: ScheduleAlarmRequest): Flow<Result<ScheduleAlarmResponse>> = flow {
        emit(fetch(HttpMethod.POST, "/alarms/setting", body = request))
    }

    override suspend fun createSchedule(request: CreateScheduleRequest): Flow<Result<Unit>> = flow {
        emit(fetch(HttpMethod.POST, "/schedules", body = request))
    }

    override suspend fun getScheduleDetail(scheduleId: Long): Flow<Result<ScheduleDetail>> = flow {
        emit(fetch(HttpMethod.GET, "/schedules/$scheduleId"))
    }

    override suspend fun deleteSchedule(scheduleId: Long): Flow<Result<Unit>> = flow {
        emit(fetch(HttpMethod.DELETE, "/schedules/$scheduleId"))
    }

    override suspend fun editSchedule(
        scheduleId: Long,
        request: ScheduleDetail
    ): Flow<Result<Unit>> = flow {
        emit(fetch(HttpMethod.PUT, "/schedules/$scheduleId", body = request))
    }

    override suspend fun toggleAlarm(scheduleId: Long, request: ToggleAlarmRequest): Flow<Result<Unit>> = flow {
        emit(fetch(HttpMethod.PATCH, "/schedules/$scheduleId/alarm", body = request))
    }

    override suspend fun getLocalScheduleById(scheduleId: Long): Flow<Schedule?> {
        return local.observeById(scheduleId)
    }

    override suspend fun upsertLocalSchedule(schedule: Schedule) {
        local.upsert(schedule)
    }
}
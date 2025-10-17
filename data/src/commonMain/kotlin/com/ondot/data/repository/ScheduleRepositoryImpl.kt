package com.ondot.data.repository

import com.ondot.data.mapper.ScheduleAlarmResponseMapper
import com.ondot.data.mapper.ScheduleDetailResponseMapper
import com.ondot.data.mapper.ScheduleListResponseMapper
import com.ondot.data.mapper.SchedulePreparationResponseMapper
import com.ondot.domain.datasource.ScheduleLocalDataSource
import com.ondot.domain.model.request.CreateScheduleRequest
import com.ondot.domain.model.request.ScheduleAlarmRequest
import com.ondot.domain.model.request.ToggleAlarmRequest
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.model.schedule.ScheduleAlarm
import com.ondot.domain.model.schedule.ScheduleDetail
import com.ondot.domain.model.schedule.ScheduleList
import com.ondot.domain.model.schedule.SchedulePreparation
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.network.HttpMethod
import com.ondot.network.NetworkClient
import com.ondot.network.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ScheduleRepositoryImpl(
    networkClient: NetworkClient,
    private val local: ScheduleLocalDataSource
) : ScheduleRepository, BaseRepository(networkClient) {

    /**
     * Remote
     * */
    override suspend fun getScheduleList(): Flow<Result<ScheduleList>> = flow {
        val remote = fetchMapped(HttpMethod.GET, "/schedules", mapper = ScheduleListResponseMapper)

        emit(remote)

        remote.onSuccess {
            local.upsertAll(it.scheduleList)
        }
    }

    override suspend fun getScheduleAlarms(request: ScheduleAlarmRequest): Flow<Result<ScheduleAlarm>> = flow {
        emit(fetchMapped(HttpMethod.POST, "/alarms/setting", body = request, mapper = ScheduleAlarmResponseMapper))
    }

    override suspend fun createSchedule(request: CreateScheduleRequest): Flow<Result<Unit>> = flow {
        emit(fetch(HttpMethod.POST, "/schedules", body = request))
    }

    override suspend fun getScheduleDetail(scheduleId: Long): Flow<Result<ScheduleDetail>> = flow {
        emit(fetchMapped(HttpMethod.GET, "/schedules/$scheduleId", mapper = ScheduleDetailResponseMapper))
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

    override suspend fun getSchedulePreparationInfo(scheduleId: Long): Flow<Result<SchedulePreparation>> = flow {
        emit(fetchMapped(HttpMethod.GET, "/schedules/$scheduleId/preparation", mapper = SchedulePreparationResponseMapper))
    }

    /**
     * Local
     * */
    override suspend fun getLocalScheduleById(scheduleId: Long): Flow<Schedule?> {
        return local.observeById(scheduleId)
    }

    override suspend fun upsertLocalSchedule(schedule: Schedule) {
        local.upsert(schedule)
    }
}
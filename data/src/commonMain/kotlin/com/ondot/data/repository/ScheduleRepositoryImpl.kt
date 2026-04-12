package com.ondot.data.repository

import com.ondot.data.mapper.ScheduleAlarmResponseMapper
import com.ondot.data.mapper.ScheduleDetailResponseMapper
import com.ondot.data.mapper.ScheduleListResponseMapper
import com.ondot.data.mapper.SchedulePreparationResponseMapper
import com.ondot.data.model.request.everytime.EverytimeValidateRequest
import com.ondot.data.model.request.everytime.mapper.toRequest
import com.ondot.data.model.response.schedule.EverytimeValidateResponse
import com.ondot.data.model.response.schedule.mapper.toDomain
import com.ondot.domain.datasource.ScheduleLocalDataSource
import com.ondot.domain.model.command.CreateEverytimeScheduleCommand
import com.ondot.domain.model.request.CreateScheduleRequest
import com.ondot.domain.model.request.ScheduleAlarmRequest
import com.ondot.domain.model.request.ToggleAlarmRequest
import com.ondot.domain.model.schedule.EverytimeValidateTimetable
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.model.schedule.ScheduleAlarm
import com.ondot.domain.model.schedule.ScheduleDetail
import com.ondot.domain.model.schedule.ScheduleList
import com.ondot.domain.model.schedule.SchedulePreparation
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.network.HttpMethod
import com.ondot.network.NetworkClient
import com.ondot.network.base.BaseRepository
import com.ondot.network.execute.safeApiCall
import com.ondot.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ScheduleRepositoryImpl(
    private val networkClient: NetworkClient,
    private val local: ScheduleLocalDataSource,
) : BaseRepository(networkClient),
    ScheduleRepository {
    /**
     * Remote
     * */
    override suspend fun getScheduleList(): Flow<Result<ScheduleList>> =
        flow {
            val remote = fetchMapped(HttpMethod.GET, "/schedules", mapper = ScheduleListResponseMapper)

            emit(remote)

            remote.onSuccess {
                local.upsertAll(it.scheduleList)
            }
        }

    override suspend fun getScheduleAlarms(request: ScheduleAlarmRequest): Flow<Result<ScheduleAlarm>> =
        flow {
            emit(fetchMapped(HttpMethod.POST, "/alarms/setting", body = request, mapper = ScheduleAlarmResponseMapper))
        }

    override suspend fun createSchedule(request: CreateScheduleRequest): Flow<Result<Unit>> =
        flow {
            emit(fetch(HttpMethod.POST, "/schedules", body = request))
        }

    override suspend fun getScheduleDetail(scheduleId: Long): Flow<Result<ScheduleDetail>> =
        flow {
            emit(fetchMapped(HttpMethod.GET, "/schedules/$scheduleId", mapper = ScheduleDetailResponseMapper))
        }

    override suspend fun deleteSchedule(scheduleId: Long): Flow<Result<Unit>> =
        flow {
            emit(fetch(HttpMethod.DELETE, "/schedules/$scheduleId"))
        }

    override suspend fun editSchedule(
        scheduleId: Long,
        request: ScheduleDetail,
    ): Flow<Result<Unit>> =
        flow {
            emit(fetch(HttpMethod.PUT, "/schedules/$scheduleId", body = request))
        }

    override suspend fun toggleAlarm(
        scheduleId: Long,
        request: ToggleAlarmRequest,
    ): Flow<Result<Unit>> =
        flow {
            emit(fetch(HttpMethod.PATCH, "/schedules/$scheduleId/alarm", body = request))
        }

    override suspend fun getSchedulePreparationInfo(scheduleId: Long): Flow<Result<SchedulePreparation>> =
        flow {
            emit(fetchMapped(HttpMethod.GET, "/schedules/$scheduleId/preparation", mapper = SchedulePreparationResponseMapper))
        }

    override suspend fun validateEverytimeTimetable(url: String): AppResult<EverytimeValidateTimetable> =
        safeApiCall {
            networkClient
                .requestOrThrow<EverytimeValidateResponse>(
                    method = HttpMethod.POST,
                    path = "/schedules/everytime/validate",
                    body = EverytimeValidateRequest(url),
                ).toDomain()
        }

    override suspend fun createEverytimeSchedule(command: CreateEverytimeScheduleCommand): AppResult<Unit> =
        safeApiCall {
            networkClient.requestOrThrow<Unit>(
                method = HttpMethod.POST,
                path = "/schedules/everytime",
                body = command.toRequest(),
            )
        }

    override suspend fun toggleAlarm(
        scheduleId: Long,
        isEnabled: Boolean,
    ): AppResult<Unit> =
        safeApiCall {
            networkClient.requestOrThrow<Unit>(
                method = HttpMethod.PATCH,
                path = "/schedules/$scheduleId/alarm",
                body = ToggleAlarmRequest(isEnabled),
            )
        }

    override suspend fun deleteScheduleAppResult(scheduleId: Long): AppResult<Unit> =
        safeApiCall {
            networkClient.requestOrThrow<Unit>(
                method = HttpMethod.DELETE,
                path = "/schedules/$scheduleId",
            )
        }

    /**
     * Local
     * */
    override suspend fun getLocalScheduleById(scheduleId: Long): Flow<Schedule?> = local.observeById(scheduleId)

    override suspend fun upsertLocalSchedule(schedule: Schedule) {
        local.upsert(schedule)
    }
}

package com.ondot.domain.repository

import com.ondot.domain.model.request.CreateScheduleRequest
import com.ondot.domain.model.request.ScheduleAlarmRequest
import com.ondot.domain.model.request.ToggleAlarmRequest
import com.ondot.domain.model.response.Schedule
import com.ondot.domain.model.response.ScheduleAlarmResponse
import com.ondot.domain.model.response.ScheduleDetail
import com.ondot.domain.model.response.ScheduleListResponse
import com.ondot.domain.model.schedule.SchedulePreparation
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    /**
     * Remote
     * */
    suspend fun getScheduleList(): Flow<Result<ScheduleListResponse>>
    suspend fun getScheduleAlarms(request: ScheduleAlarmRequest): Flow<Result<ScheduleAlarmResponse>>
    suspend fun createSchedule(request: CreateScheduleRequest): Flow<Result<Unit>>
    suspend fun getScheduleDetail(scheduleId: Long): Flow<Result<ScheduleDetail>>
    suspend fun deleteSchedule(scheduleId: Long): Flow<Result<Unit>>
    suspend fun editSchedule(scheduleId: Long, request: ScheduleDetail): Flow<Result<Unit>>
    suspend fun toggleAlarm(scheduleId: Long, request: ToggleAlarmRequest): Flow<Result<Unit>>
    suspend fun getSchedulePreparationInfo(scheduleId: Long): Flow<Result<SchedulePreparation>>

    /**
     * Local
     * */
    suspend fun getLocalScheduleById(scheduleId: Long): Flow<Schedule?>
    suspend fun upsertLocalSchedule(schedule: Schedule)
}
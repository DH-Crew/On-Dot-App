package com.dh.ondot.domain.repository

import com.dh.ondot.domain.model.request.CreateScheduleRequest
import com.dh.ondot.domain.model.request.ScheduleAlarmRequest
import com.dh.ondot.domain.model.request.ToggleAlarmRequest
import com.dh.ondot.domain.model.response.Schedule
import com.dh.ondot.domain.model.response.ScheduleAlarmResponse
import com.dh.ondot.domain.model.response.ScheduleDetail
import com.dh.ondot.domain.model.response.ScheduleListResponse
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    suspend fun getScheduleList(): Flow<Result<ScheduleListResponse>>
    suspend fun getScheduleAlarms(request: ScheduleAlarmRequest): Flow<Result<ScheduleAlarmResponse>>
    suspend fun createSchedule(request: CreateScheduleRequest): Flow<Result<Unit>>
    suspend fun getScheduleDetail(scheduleId: Long): Flow<Result<ScheduleDetail>>
    suspend fun deleteSchedule(scheduleId: Long): Flow<Result<Unit>>
    suspend fun editSchedule(scheduleId: Long, request: ScheduleDetail): Flow<Result<Unit>>
    suspend fun toggleAlarm(scheduleId: Long, request: ToggleAlarmRequest): Flow<Result<Unit>>
    suspend fun getLocalScheduleById(scheduleId: Long): Flow<Schedule?>
}
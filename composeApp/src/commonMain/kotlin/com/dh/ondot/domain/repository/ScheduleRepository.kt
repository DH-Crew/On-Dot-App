package com.dh.ondot.domain.repository

import com.dh.ondot.domain.model.request.CreateScheduleRequest
import com.dh.ondot.domain.model.request.ScheduleAlarmRequest
import com.dh.ondot.domain.model.response.ScheduleAlarmResponse
import com.dh.ondot.domain.model.response.ScheduleListResponse
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    suspend fun getScheduleList(): Flow<Result<ScheduleListResponse>>
    suspend fun getScheduleAlarms(request: ScheduleAlarmRequest): Flow<Result<ScheduleAlarmResponse>>
    suspend fun createSchedule(request: CreateScheduleRequest): Flow<Result<Unit>>
}
package com.dh.ondot.domain.repository

import com.dh.ondot.domain.model.response.ScheduleListResponse
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    suspend fun getScheduleList(): Flow<Result<ScheduleListResponse>>
}
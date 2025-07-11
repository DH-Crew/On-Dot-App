package com.dh.ondot.data.repository

import com.dh.ondot.domain.model.request.ScheduleAlarmRequest
import com.dh.ondot.domain.model.response.ScheduleAlarmResponse
import com.dh.ondot.domain.model.response.ScheduleListResponse
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.network.HttpMethod
import com.dh.ondot.network.NetworkClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ScheduleRepositoryImpl(
    private val networkClient: NetworkClient
) : ScheduleRepository {
    override suspend fun getScheduleList(): Flow<Result<ScheduleListResponse>> = flow {
        val response = networkClient.request<ScheduleListResponse>(
            method = HttpMethod.GET,
            path = "/schedules",
        )

        response.fold(
            onSuccess = { emit(Result.success(it)) },
            onFailure = { emit(Result.failure(it)) }
        )
    }

    override suspend fun getScheduleAlarms(request: ScheduleAlarmRequest): Flow<Result<ScheduleAlarmResponse>> = flow {
        val response = networkClient.request<ScheduleAlarmResponse>(
            path = "/alarms/setting",
            method = HttpMethod.POST,
            body = request
        )

        response.fold(
            onSuccess = { emit(Result.success(it)) },
            onFailure = { emit(Result.failure(it)) }
        )
    }
}
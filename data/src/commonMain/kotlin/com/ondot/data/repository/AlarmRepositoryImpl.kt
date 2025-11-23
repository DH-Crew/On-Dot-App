package com.ondot.data.repository

import com.ondot.domain.model.request.alarm.TriggeredAlarmRequest
import com.ondot.domain.repository.AlarmRepository
import com.ondot.network.HttpMethod
import com.ondot.network.NetworkClient
import com.ondot.network.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AlarmRepositoryImpl(
    private val networkClient: NetworkClient
): AlarmRepository, BaseRepository(networkClient) {
    override suspend fun recordTriggeredAlarm(request: TriggeredAlarmRequest): Flow<Result<Unit>> =
        flow {
            emit(
                fetch(
                    method = HttpMethod.POST,
                    path = "/alarms/triggers",
                    body = request
                )
            )
        }
}
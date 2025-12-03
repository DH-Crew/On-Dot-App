package com.ondot.testing.fake

import com.ondot.domain.model.request.alarm.TriggeredAlarmRequest
import com.ondot.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAlarmRepository: AlarmRepository {
    override suspend fun recordTriggeredAlarm(request: TriggeredAlarmRequest): Flow<Result<Unit>> = flow {
        emit(
            Result.success(Unit)
        )
    }
}
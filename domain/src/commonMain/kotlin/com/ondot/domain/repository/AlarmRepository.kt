package com.ondot.domain.repository

import com.ondot.domain.model.request.alarm.TriggeredAlarmRequest
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun recordTriggeredAlarm(request: TriggeredAlarmRequest): Flow<Result<Unit>>
}
package com.dh.ondot.domain.service

import com.dh.ondot.domain.model.response.AlarmDetail
import kotlinx.coroutines.flow.Flow

interface AlarmStorage {
    suspend fun saveAlarms(alarms: List<AlarmDetail>)
    val alarmsFlow: Flow<List<AlarmDetail>>
}
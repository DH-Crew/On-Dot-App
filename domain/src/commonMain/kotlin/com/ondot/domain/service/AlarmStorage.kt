package com.ondot.domain.service

import com.ondot.domain.model.ui.AlarmRingInfo
import kotlinx.coroutines.flow.Flow

interface AlarmStorage {
    suspend fun saveAlarms(alarms: List<AlarmRingInfo>)
    suspend fun addAlarm(alarm: AlarmRingInfo)
    val alarmsFlow: Flow<List<AlarmRingInfo>>
}
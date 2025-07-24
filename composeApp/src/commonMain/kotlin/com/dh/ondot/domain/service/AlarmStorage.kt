package com.dh.ondot.domain.service

import com.dh.ondot.domain.model.ui.AlarmRingInfo
import kotlinx.coroutines.flow.Flow

interface AlarmStorage {
    suspend fun saveAlarms(alarms: List<AlarmRingInfo>)
    val alarmsFlow: Flow<List<AlarmRingInfo>>
}
package com.ondot.domain.service

import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.ui.AlarmRingInfo

interface AlarmScheduler {
    fun scheduleAlarm(info: AlarmRingInfo, mapProvider: MapProvider)
    fun cancelAlarm(alarmId: Long)
    fun snoozeAlarm(alarmId: Long)
}
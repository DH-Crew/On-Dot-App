package com.dh.ondot.domain.service

import com.dh.ondot.domain.model.enums.MapProvider
import com.dh.ondot.domain.model.ui.AlarmRingInfo

interface AlarmScheduler {
    fun scheduleAlarm(info: AlarmRingInfo, mapProvider: MapProvider)
    fun cancelAlarm(alarmId: Long)
    fun snoozeAlarm(alarmId: Long)
}
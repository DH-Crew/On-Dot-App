package com.dh.ondot.domain.di

import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.model.response.AlarmDetail

interface AlarmScheduler {
    fun scheduleAlarm(alarm: AlarmDetail, type: AlarmType)
    fun cancelAlarm(alarmId: Long)
    fun snoozeAlarm(alarmId: Long)
}
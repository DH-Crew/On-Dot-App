package com.dh.ondot.domain.service

import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.model.response.AlarmDetail

interface AlarmScheduler {
    fun scheduleAlarm(scheduleId: Long, alarm: AlarmDetail, type: AlarmType)
    fun cancelAlarm(alarmId: Long)
    fun snoozeAlarm(alarmId: Long)
}
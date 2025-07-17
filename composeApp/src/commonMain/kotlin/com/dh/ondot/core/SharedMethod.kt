package com.dh.ondot.core

import com.dh.ondot.core.util.AlarmNotifier
import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.model.ui.AlarmEvent

fun notifyAlarmEvent(alarmId: Long, type: String) {
    val event = AlarmEvent(alarmId, AlarmType.valueOf(type))

    AlarmNotifier.notify(event)
}
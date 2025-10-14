package com.dh.ondot.core

import co.touchlab.kermit.Logger
import com.dh.ondot.core.util.AlarmNotifier
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.ui.AlarmEvent

fun notifyAlarmEvent(scheduleId: Long, alarmId: Long, type: String) {
    val logger = Logger.withTag("notifyAlarmEvent")
    val alarmType = runCatching { AlarmType.valueOf(type) }
        .getOrElse {
            logger.e { "유효하지 않은 AlarmType 입니다. type: $type" }
            return
        }
    val event = AlarmEvent(scheduleId, alarmId, alarmType)

    AlarmNotifier.notify(event)
}
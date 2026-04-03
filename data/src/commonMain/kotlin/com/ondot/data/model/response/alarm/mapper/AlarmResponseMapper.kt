package com.ondot.data.model.response.alarm.mapper

import com.ondot.data.model.response.alarm.AlarmResponse
import com.ondot.domain.model.alarm.Alarm

fun AlarmResponse.toDomain(): Alarm =
    Alarm(
        alarmId = alarmId,
        alarmMode = alarmMode,
        enabled = enabled,
        triggeredAt = triggeredAt,
        isSnoozeEnabled = isSnoozeEnabled,
        snoozeInterval = snoozeInterval,
        snoozeCount = snoozeCount,
        soundCategory = soundCategory,
        ringTone = ringTone,
        volume = volume,
    )

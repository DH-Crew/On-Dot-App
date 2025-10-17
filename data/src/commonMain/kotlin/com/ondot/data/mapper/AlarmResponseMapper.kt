package com.ondot.data.mapper

import com.ondot.data.model.response.alarm.AlarmResponse
import com.ondot.domain.model.alarm.Alarm
import com.ondot.network.base.Mapper

object AlarmResponseMapper: Mapper<AlarmResponse, Alarm> {
    override fun responseToModel(response: AlarmResponse?): Alarm {
        return response?.let {
            Alarm(
                alarmId = it.alarmId,
                alarmMode = it.alarmMode,
                enabled = it.enabled,
                triggeredAt = it.triggeredAt,
                isSnoozeEnabled = it.isSnoozeEnabled,
                snoozeInterval = it.snoozeInterval,
                snoozeCount = it.snoozeCount,
                soundCategory = it.soundCategory,
                ringTone = it.ringTone,
                volume = it.volume
            )
        } ?: Alarm()
    }
}
package com.ondot.testing.fake.util

import co.touchlab.kermit.Logger
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.ui.AlarmRingInfo
import com.ondot.domain.service.AlarmCancelResult
import com.ondot.domain.service.AlarmScheduleResult
import com.ondot.domain.service.AlarmScheduler

class FakeAlarmScheduler : AlarmScheduler {
    private val logger = Logger.withTag("FakeAlarmScheduler")
    val scheduled = mutableListOf<Pair<AlarmRingInfo, MapProvider>>()
    val cancelledIds = mutableListOf<Long>()

    override suspend fun scheduleAlarm(
        info: AlarmRingInfo,
        mapProvider: MapProvider,
    ): AlarmScheduleResult {
        if (!scheduled.any { it.first.alarm.alarmId == info.alarm.alarmId }) {
            logger.e { "scheduleAlarm: $info" }
            scheduled += info to mapProvider
        }

        return AlarmScheduleResult.Success(platformId = info.alarm.alarmId.toString())
    }

    override suspend fun cancelAlarm(alarmId: Long): AlarmCancelResult {
        val removed = scheduled.removeAll { it.first.alarm.alarmId == alarmId }
        cancelledIds += alarmId

        return if (removed) {
            AlarmCancelResult.Success
        } else {
            AlarmCancelResult.NotFound
        }
    }

    override suspend fun snoozeAlarm(alarmId: Long): AlarmScheduleResult = AlarmScheduleResult.Success(platformId = alarmId.toString())

    fun clear() {
        scheduled.clear()
        cancelledIds.clear()
    }
}

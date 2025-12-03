package com.ondot.testing.fake.util

import co.touchlab.kermit.Logger
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.ui.AlarmRingInfo
import com.ondot.domain.service.AlarmScheduler

class FakeAlarmScheduler: AlarmScheduler {
    private val logger = Logger.withTag("FakeAlarmScheduler")
    val scheduled = mutableListOf<Pair<AlarmRingInfo, MapProvider>>()
    val cancelledIds = mutableListOf<Long>()

    override fun scheduleAlarm(
        info: AlarmRingInfo,
        mapProvider: MapProvider
    ) {
        logger.e { "scheduleAlarm: $info" }
        scheduled += info to mapProvider
    }

    override fun cancelAlarm(alarmId: Long) {
        cancelledIds += alarmId
    }

    override fun snoozeAlarm(alarmId: Long) {
        TODO("Not yet implemented")
    }

    fun clear() {
        scheduled.clear()
        cancelledIds.clear()
    }
}
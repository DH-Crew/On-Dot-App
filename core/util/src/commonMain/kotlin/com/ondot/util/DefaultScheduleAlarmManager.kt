package com.ondot.util

import com.ondot.bridge.TriggeredAlarmManager
import com.ondot.domain.model.enums.AlarmAction
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.model.ui.AlarmRingInfo
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.service.AlarmScheduler
import com.ondot.domain.service.ScheduleAlarmManager
import kotlinx.coroutines.flow.first

class DefaultScheduleAlarmManager(
    private val alarmScheduler: AlarmScheduler,
    private val memberRepository: MemberRepository,
) : ScheduleAlarmManager {
    override suspend fun syncAll(
        schedules: List<Schedule>,
        previouslyScheduledAlarmIds: Set<Long>,
    ): Set<Long> {
        val mapProvider = currentMapProvider()

        val currentAlarmIds =
            schedules
                .flatMap { schedule -> enabledAlarmIds(schedule) }
                .toSet()

        val toCancel = previouslyScheduledAlarmIds - currentAlarmIds
        toCancel.forEach { alarmId ->
            alarmScheduler.cancelAlarm(alarmId)
        }

        schedules
            .flatMap { schedule -> enabledAlarmInfos(schedule) }
            .forEach { info ->
                alarmScheduler.scheduleAlarm(
                    info = info,
                    mapProvider = mapProvider,
                )
                TriggeredAlarmManager.recordTriggeredAlarm(
                    info.scheduleId,
                    info.alarm.alarmId,
                    AlarmAction.SCHEDULED,
                )
            }

        return currentAlarmIds
    }

    override suspend fun schedule(schedule: Schedule) {
        val mapProvider = currentMapProvider()

        enabledAlarmInfos(schedule).forEach { info ->
            alarmScheduler.scheduleAlarm(
                info = info,
                mapProvider = mapProvider,
            )
            TriggeredAlarmManager.recordTriggeredAlarm(
                info.scheduleId,
                info.alarm.alarmId,
                AlarmAction.SCHEDULED,
            )
        }
    }

    override suspend fun cancel(schedule: Schedule) {
        alarmScheduler.cancelAlarm(schedule.preparationAlarm.alarmId)
        alarmScheduler.cancelAlarm(schedule.departureAlarm.alarmId)
        TriggeredAlarmManager.recordTriggeredAlarm(
            schedule.scheduleId,
            schedule.preparationAlarm.alarmId,
            AlarmAction.STOP,
        )
        TriggeredAlarmManager.recordTriggeredAlarm(
            schedule.scheduleId,
            schedule.departureAlarm.alarmId,
            AlarmAction.STOP,
        )
    }

    override suspend fun applyToggle(
        original: Schedule,
        enabled: Boolean,
    ) {
        if (enabled) {
            val updated =
                original.copy(
                    hasActiveAlarm = true,
                    preparationAlarm = original.preparationAlarm.copy(enabled = true),
                    departureAlarm = original.departureAlarm.copy(enabled = true),
                )
            schedule(updated)
        } else {
            cancel(original)
        }
    }

    override suspend fun replace(
        original: Schedule,
        updated: Schedule,
    ): Result<Unit> =
        runCatching {
            cancel(original)
            if (updated.hasActiveAlarm) {
                schedule(updated)
            }
        }

    private suspend fun currentMapProvider(): MapProvider = memberRepository.getLocalMapProvider().first()

    private fun enabledAlarmIds(schedule: Schedule): List<Long> =
        buildList {
            if (schedule.preparationAlarm.enabled) add(schedule.preparationAlarm.alarmId)
            if (schedule.departureAlarm.enabled) add(schedule.departureAlarm.alarmId)
        }

    private fun enabledAlarmInfos(schedule: Schedule): List<AlarmRingInfo> =
        buildList {
            if (schedule.preparationAlarm.enabled) add(schedule.toPreparationInfo())
            if (schedule.departureAlarm.enabled) add(schedule.toDepartureInfo())
        }
}

private fun Schedule.toPreparationInfo() =
    AlarmRingInfo(
        alarm = preparationAlarm,
        alarmType = AlarmType.Preparation,
        appointmentAt = appointmentAt,
        scheduleTitle = scheduleTitle,
        scheduleId = scheduleId,
        startLat = startLatitude,
        startLng = startLongitude,
        endLat = endLatitude,
        endLng = endLongitude,
        repeatDays = repeatDays,
    )

private fun Schedule.toDepartureInfo() =
    AlarmRingInfo(
        alarm = departureAlarm,
        alarmType = AlarmType.Departure,
        appointmentAt = appointmentAt,
        scheduleTitle = scheduleTitle,
        scheduleId = scheduleId,
        startLat = startLatitude,
        startLng = startLongitude,
        endLat = endLatitude,
        endLng = endLongitude,
        repeatDays = repeatDays,
    )

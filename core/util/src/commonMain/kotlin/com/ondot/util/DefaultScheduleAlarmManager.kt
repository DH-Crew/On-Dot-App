package com.ondot.util

import co.touchlab.kermit.Logger
import com.ondot.bridge.TriggeredAlarmManager
import com.ondot.domain.model.enums.AlarmAction
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.model.ui.AlarmRingInfo
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.service.AlarmCancelResult
import com.ondot.domain.service.AlarmScheduleResult
import com.ondot.domain.service.AlarmScheduler
import com.ondot.domain.service.ScheduleAlarmManager
import kotlinx.coroutines.flow.first
import kotlin.time.ExperimentalTime

class DefaultScheduleAlarmManager(
    private val alarmScheduler: AlarmScheduler,
    private val memberRepository: MemberRepository,
) : ScheduleAlarmManager {
    private val logger = Logger.withTag("DefaultScheduleAlarmManager")

    override suspend fun syncAll(
        schedules: List<Schedule>,
        previouslyScheduledAlarmIds: Set<Long>,
    ): Set<Long> {
        val mapProvider = currentMapProvider()
        val dedupedAlarmInfos =
            dedupeAlarmInfos(
                schedules.flatMap { schedule -> enabledAlarmInfos(schedule) },
            )
        val currentAlarmIds = dedupedAlarmInfos.map { it.alarm.alarmId }.toSet()

        val toCancel = previouslyScheduledAlarmIds - currentAlarmIds
        val failedCancelIds = mutableSetOf<Long>()
        toCancel.forEach { alarmId ->
            when (val result = alarmScheduler.cancelAlarm(alarmId)) {
                AlarmCancelResult.Success,
                AlarmCancelResult.NotFound,
                -> {
                    logger.d { "syncAll cancel ok-ish: alarmId=$alarmId, result=$result" }
                }
                is AlarmCancelResult.Failure -> {
                    logger.e { "syncAll cancel fail: alarmId=$alarmId, reason=${result.reason}" }
                    failedCancelIds += alarmId
                }
            }
        }

        scheduleInfos(
            infos = dedupedAlarmInfos,
            mapProvider = mapProvider,
            throwOnFailure = false,
        )

        return currentAlarmIds + failedCancelIds
    }

    override suspend fun schedule(schedule: Schedule) {
        val mapProvider = currentMapProvider()
        val dedupedAlarmInfos = dedupeAlarmInfos(enabledAlarmInfos(schedule))

        scheduleInfos(
            infos = dedupedAlarmInfos,
            mapProvider = mapProvider,
            throwOnFailure = true,
        )
    }

    override suspend fun cancel(schedule: Schedule) {
        cancelSingle(schedule.scheduleId, schedule.preparationAlarm.alarmId)
        cancelSingle(schedule.scheduleId, schedule.departureAlarm.alarmId)
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
        try {
            cancel(original)

            if (updated.hasActiveAlarm) {
                schedule(updated)
            }

            Result.success(Unit)
        } catch (t: Throwable) {
            if (enabledAlarmIds(original).isNotEmpty()) {
                runCatching { schedule(original) }
            }
            Result.failure(t)
        }

    private suspend fun scheduleInfos(
        infos: List<AlarmRingInfo>,
        mapProvider: MapProvider,
        throwOnFailure: Boolean,
    ) {
        infos.forEach { info ->
            when (val result = alarmScheduler.scheduleAlarm(info, mapProvider)) {
                is AlarmScheduleResult.Success -> {
                    TriggeredAlarmManager.recordTriggeredAlarm(
                        info.scheduleId,
                        info.alarm.alarmId,
                        AlarmAction.SCHEDULED,
                    )
                }
                else -> {
                    val message =
                        "schedule fail: scheduleId=${info.scheduleId}, alarmId=${info.alarm.alarmId}, result=$result"

                    if (throwOnFailure) {
                        throw IllegalStateException(message)
                    } else {
                        logger.e { message }
                    }
                }
            }
        }
    }

    private suspend fun cancelSingle(
        scheduleId: Long,
        alarmId: Long,
    ) {
        when (val result = alarmScheduler.cancelAlarm(alarmId)) {
            AlarmCancelResult.Success,
            AlarmCancelResult.NotFound,
            -> {
                TriggeredAlarmManager.recordTriggeredAlarm(
                    scheduleId,
                    alarmId,
                    AlarmAction.STOP,
                )
            }
            is AlarmCancelResult.Failure -> {
                throw IllegalStateException(
                    "Failed to cancel alarmId=$alarmId, reason=${result.reason}",
                )
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun dedupeAlarmInfos(infos: List<AlarmRingInfo>): List<AlarmRingInfo> {
        val now =
            kotlin.time.Clock.System
                .now()
                .toEpochMilliseconds()

        return infos
            .groupBy { it.alarm.alarmId }
            .mapNotNull { (_, sameAlarmInfos) ->
                sameAlarmInfos
                    .mapNotNull { info ->
                        runCatching {
                            info to DateTimeFormatter.isoStringToEpochMillis(info.alarm.triggeredAt)
                        }.getOrNull()
                    }.sortedWith(
                        compareBy<Pair<AlarmRingInfo, Long>> { (_, triggerAt) ->
                            if (triggerAt >= now) 0 else 1
                        }.thenBy { (_, triggerAt) ->
                            if (triggerAt >= now) triggerAt else Long.MAX_VALUE
                        }.thenByDescending { (_, triggerAt) ->
                            if (triggerAt < now) triggerAt else Long.MIN_VALUE
                        },
                    ).firstOrNull()
                    ?.first
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

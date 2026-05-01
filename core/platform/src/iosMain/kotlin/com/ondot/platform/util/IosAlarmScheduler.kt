package com.ondot.platform.util

import co.touchlab.kermit.Logger
import com.dh.ondot.alarmkit.ONDAlarmKit
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.ui.AlarmRingInfo
import com.ondot.domain.service.AlarmCancelResult
import com.ondot.domain.service.AlarmScheduleResult
import com.ondot.domain.service.AlarmScheduler
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDateComponents
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone
import platform.Foundation.localeWithLocaleIdentifier
import platform.Foundation.numberWithDouble
import platform.Foundation.timeZoneWithName
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val ALARMKIT_CANCEL_TIMEOUT_MS = 3_000L
private const val ALARMKIT_SCHEDULE_TIMEOUT_MS = 5_000L

@OptIn(ExperimentalForeignApi::class)
class IosAlarmScheduler : AlarmScheduler {
    private val logger = Logger.withTag("IOSAlarmScheduler")
    private var isAuthorized: Boolean? = null
    private val authMutex = Mutex()

    /*
     * schedule / cancel / snooze 전부 동일한 직렬화 경로를 타게 한다.
     * 이 mutex 바깥에서 AlarmKit 상태 변경 작업을 하지 않는다.
     */
    private val operationMutex = Mutex()

    private suspend fun ensureAuthorization(): Boolean =
        authMutex.withLock {
            isAuthorized?.let { return it }

            suspendCoroutine { cont ->
                ONDAlarmKit.requestAuthorization { ok ->
                    isAuthorized = ok
                    cont.resume(ok)
                }
            }
        }

    override suspend fun scheduleAlarm(
        info: AlarmRingInfo,
        mapProvider: MapProvider,
    ): AlarmScheduleResult =
        operationMutex.withLock {
            if (!ensureAuthorization()) {
                logger.e { "AlarmKit: authorization denied" }
                return@withLock AlarmScheduleResult.Unauthorized
            }

            val alarm = info.alarm
            if (!alarm.enabled) {
                logger.d { "AlarmKit: skip disabled alarmId=${alarm.alarmId}" }
                return@withLock AlarmScheduleResult.SkippedDisabled
            }

            val comps = isoStringToDateComponents(alarm.triggeredAt)
            if (comps == null) {
                logger.e { "AlarmKit: invalid date=${alarm.triggeredAt}" }
                return@withLock AlarmScheduleResult.InvalidDate
            }

            val title =
                when (info.alarmType) {
                    AlarmType.Preparation -> "준비 알람"
                    AlarmType.Departure -> "출발 알람"
                }

            // 알람킷 브릿지가
            try {
                withTimeout(ALARMKIT_SCHEDULE_TIMEOUT_MS) {
                    suspendCoroutine<AlarmScheduleResult> { cont ->
                        ONDAlarmKit.scheduleCalendarWithId(
                            alarmUUID = alarm.alarmId.toString(),
                            dateComponents = comps,
                            repeatDays = info.repeatDays,
                            title = title,
                            scheduleId = info.scheduleId,
                            alarmId = alarm.alarmId,
                            alarmType = info.alarmType.name.lowercase(),
                            tintHex = null,
                            openMapsOnSecondary = info.alarmType == AlarmType.Departure,
                            startLat = info.startLat.toNSNumber(),
                            startLng = info.startLng.toNSNumber(),
                            endLat = info.endLat.toNSNumber(),
                            endLng = info.endLng.toNSNumber(),
                            mapProvider = mapProvider.name.lowercase(),
                        ) { uuid, err ->
                            if (err != null) {
                                logger.e { "AlarmKit schedule FAIL: alarmId=${alarm.alarmId}, err=$err" }
                                cont.resume(AlarmScheduleResult.Failure(err))
                            } else {
                                logger.d { "AlarmKit scheduled: alarmId=${alarm.alarmId}, uuid=$uuid" }
                                cont.resume(AlarmScheduleResult.Success(uuid))
                            }
                        }
                    }
                }
            } catch (_: TimeoutCancellationException) {
                logger.e { "AlarmKit schedule timeout: alarmId=${alarm.alarmId}" }
                AlarmScheduleResult.Failure("timeout")
            }
        }

    override suspend fun cancelAlarm(alarmId: Long): AlarmCancelResult =
        operationMutex.withLock {
            if (!ensureAuthorization()) {
                logger.e { "AlarmKit: authorization denied on cancel, alarmId=$alarmId" }
                return@withLock AlarmCancelResult.Failure("authorization denied")
            }

            try {
                withTimeout(ALARMKIT_CANCEL_TIMEOUT_MS) {
                    suspendCoroutine<AlarmCancelResult> { cont ->
                        /*
                         * status:
                         * - "success"
                         * - "not_found"
                         * - "failure"
                         */
                        ONDAlarmKit.cancelWithId(
                            alarmUUID = alarmId.toString(),
                        ) { status, error ->
                            when (status) {
                                "success" -> {
                                    logger.d { "AlarmKit cancel success: alarmId=$alarmId" }
                                    cont.resume(AlarmCancelResult.Success)
                                }
                                "not_found" -> {
                                    logger.d { "AlarmKit cancel not found: alarmId=$alarmId" }
                                    cont.resume(AlarmCancelResult.NotFound)
                                }
                                else -> {
                                    logger.e { "AlarmKit cancel fail: alarmId=$alarmId, error=$error" }
                                    cont.resume(AlarmCancelResult.Failure(error))
                                }
                            }
                        }
                    }
                }
            } catch (_: TimeoutCancellationException) {
                logger.e { "AlarmKit cancel timeout: alarmId=$alarmId" }
                AlarmCancelResult.Failure("timeout")
            }
        }

    override suspend fun snoozeAlarm(alarmId: Long): AlarmScheduleResult = AlarmScheduleResult.Failure("snooze not implemented on iOS yet")

    private fun isoStringToDateComponents(iso: String): NSDateComponents? {
        val kst = NSTimeZone.timeZoneWithName("Asia/Seoul") ?: NSTimeZone.localTimeZone
        val fmt =
            NSDateFormatter().apply {
                locale = NSLocale.localeWithLocaleIdentifier("en_US_POSIX")
                timeZone = kst
                dateFormat =
                    if (iso.contains("Z") || iso.contains("+")) {
                        "yyyy-MM-dd'T'HH:mm:ssZ"
                    } else {
                        "yyyy-MM-dd'T'HH:mm:ss"
                    }
            }
        val date = fmt.dateFromString(iso) ?: return null
        val cal = NSCalendar.currentCalendar
        return cal.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or
                NSCalendarUnitHour or NSCalendarUnitMinute,
            fromDate = date,
        )
    }

    private fun Double?.toNSNumber(): NSNumber? = this?.let { NSNumber.numberWithDouble(it) }
}

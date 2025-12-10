package com.ondot.platform.util

import co.touchlab.kermit.Logger
import com.dh.ondot.alarmkit.ONDAlarmKit
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.ui.AlarmRingInfo
import com.ondot.domain.service.AlarmScheduler
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

@OptIn(ExperimentalForeignApi::class)
class IosAlarmScheduler(): AlarmScheduler {
    private val logger = Logger.withTag("IOSAlarmScheduler")
    private var isAuthorized: Boolean? = null
    private val authMutex = Mutex()
    private val schedulingScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val schedulingMutex = Mutex()  // 동시 접근 방지

    private suspend fun ensureAuthorization(): Boolean =
        authMutex.withLock {
            isAuthorized?.let { return it }

            suspendCancellableCoroutine { cont ->
                ONDAlarmKit.requestAuthorization { ok ->
                    isAuthorized = ok
                    if (cont.isActive) cont.resume(ok)
                }
            }
        }

    override fun scheduleAlarm(info: AlarmRingInfo, mapProvider: MapProvider) {
        schedulingScope.launch {
            if (!ensureAuthorization()) {
                logger.e("AlarmKit: authorization denied")
                return@launch
            }

            schedulingMutex.withLock {
                val alarm = info.alarm
                if (!alarm.enabled) return@withLock

                val comps = isoStringToDateComponents(alarm.triggeredAt)
                if (comps == null) {
                    logger.e("AlarmKit: invalid date: ${alarm.triggeredAt}")
                    return@withLock
                }

                val title = when (info.alarmType) {
                    AlarmType.Preparation -> "준비 알람"
                    AlarmType.Departure -> "출발 알람"
                }

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
                    mapProvider = mapProvider.name.lowercase()
                ) { uuid, err ->
                    if (err != null) logger.e("AlarmKit schedule FAIL: $err")
                    else logger.d("AlarmKit scheduled: $uuid")
                }
            }
        }
    }

    override fun cancelAlarm(alarmId: Long) {
        ONDAlarmKit.cancelWithId(alarmId.toString()) { ok ->
            logger.d { "[알람 삭제 여부] alarmId: $alarmId, ok: $ok" }
        }
    }

    override fun snoozeAlarm(alarmId: Long) {
    }

    private fun isoStringToDateComponents(iso: String): NSDateComponents? {
        val kst = NSTimeZone.timeZoneWithName("Asia/Seoul") ?: NSTimeZone.localTimeZone
        val fmt = NSDateFormatter().apply {
            locale = NSLocale.localeWithLocaleIdentifier("en_US_POSIX")
            timeZone = kst
            dateFormat = if (iso.contains("Z") || iso.contains("+")) {
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
            fromDate = date
        )
    }

    private fun Double?.toNSNumber(): NSNumber? = this?.let { NSNumber.numberWithDouble(it) }
}
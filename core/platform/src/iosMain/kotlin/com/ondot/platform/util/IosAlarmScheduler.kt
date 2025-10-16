package com.ondot.platform.util

import co.touchlab.kermit.Logger
import com.dh.ondot.alarmkit.ONDAlarmKit
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.ui.AlarmRingInfo
import com.ondot.domain.service.AlarmScheduler
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDateComponents
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSNumber
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone
import platform.Foundation.numberWithDouble

@OptIn(ExperimentalForeignApi::class)
class IosAlarmScheduler(): AlarmScheduler {
    private val logger = Logger.withTag("IOSAlarmScheduler")

    override fun scheduleAlarm(info: AlarmRingInfo, mapProvider: MapProvider) {
        val alarm = info.alarmDetail

        if (!alarm.enabled) return

        logger.d {
            "startLat: ${info.startLat}, startLng: ${info.startLng}, endLat: ${info.endLat}, endLng: ${info.endLng}"
        }

        ONDAlarmKit.requestAuthorization { ok ->
            if (!ok) {
                println("AlarmKit: authorization denied")
                return@requestAuthorization
            }

            // ISO → DateComponents
            val comps = isoStringToDateComponents(alarm.triggeredAt) ?: run {
                println("AlarmKit: invalid date: ${alarm.triggeredAt}")
                return@requestAuthorization
            }

            // 타이틀/타입
            val title = when (info.alarmType) {
                AlarmType.Preparation -> "준비 알람"
                AlarmType.Departure -> "출발 알람"
                else -> "알람"
            }

            ONDAlarmKit.scheduleCalendarWithId(
                alarmUUID = alarm.alarmId.toString(),
                dateComponents = comps,
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
                if (err != null) println("AlarmKit schedule FAIL: $err")
                else println("AlarmKit scheduled: $uuid")
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
        val fmt = NSDateFormatter().apply {
            dateFormat = if (iso.contains("Z") || iso.contains("+")) {
                "yyyy-MM-dd'T'HH:mm:ssZ"
            } else {
                "yyyy-MM-dd'T'HH:mm:ss"
            }
            timeZone = NSTimeZone.localTimeZone
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
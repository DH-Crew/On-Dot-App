package com.dh.ondot.core.util

import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.model.response.AlarmDetail
import com.dh.ondot.domain.service.AlarmScheduler
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDictionary
import platform.Foundation.NSTimeZone
import platform.Foundation.dictionaryWithObjects
import platform.Foundation.localTimeZone
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

class IosAlarmScheduler: AlarmScheduler {
    private val center = UNUserNotificationCenter.currentNotificationCenter()

    override fun scheduleAlarm(scheduleId: Long, alarm: AlarmDetail, type: AlarmType) {
        // 사용자가 끄면 아무 것도 안 함
        if (!alarm.enabled) return

        // ISO 문자열 → NSDate
        val fireDate = isoStringToNSDate(alarm.triggeredAt) ?: return

        // NSDate → NSDateComponents
        val calendar = NSCalendar.currentCalendar
        val comps = calendar.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or
                    NSCalendarUnitHour or NSCalendarUnitMinute or NSCalendarUnitSecond,
            fromDate = fireDate
        )

        // 노티 콘텐츠 설정
        val content = UNMutableNotificationContent().apply {
            setTitle("⏰ 알람")
            setBody("예약된 알람 시간이 되었습니다.")
            setSound(UNNotificationSound.soundNamed(extractSoundFileName(alarm.ringTone)))
            // NSDictionary 로 변환
            val userInfoDict = NSDictionary.dictionaryWithObjects(
                objects = listOf(scheduleId, alarm.alarmId, type.name),
                forKeys =   listOf("scheduleId", "alarmId", "type")
            )
            setUserInfo(userInfoDict)
        }

        // 트리거 생성 (한 번만)
        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = comps,
            repeats = true
        )

        // 요청 빌드
        val request = UNNotificationRequest.requestWithIdentifier(
            /* id = */ alarm.alarmId.toString(),
            /* content = */ content,
            /* trigger = */ trigger
        )

        // 스케줄 등록
        center.addNotificationRequest(request) { error ->
            error?.let {
                println("IosAlarmScheduler: 알람 등록 실패: ${it.localizedDescription}")
            }
        }
    }

    override fun cancelAlarm(alarmId: Long) {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(alarmId.toString()))
    }

    override fun snoozeAlarm(alarmId: Long) {
        TODO("Not yet implemented")
    }

    private fun isoStringToNSDate(iso: String): NSDate? {
        val fmt = NSDateFormatter().apply {
            dateFormat = if (iso.contains("Z") || iso.contains("+")) {
                "yyyy-MM-dd'T'HH:mm:ssZ"
            } else {
                "yyyy-MM-dd'T'HH:mm:ss"
            }
            timeZone = NSTimeZone.localTimeZone
        }
        return fmt.dateFromString(iso)
    }

    private fun extractSoundFileName(ringTone: String): String {
        return ringTone
            .substringBeforeLast('.')
            .lowercase()
    }
}
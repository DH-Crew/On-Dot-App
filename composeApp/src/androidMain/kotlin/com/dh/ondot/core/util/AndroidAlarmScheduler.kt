package com.dh.ondot.core.util

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import co.touchlab.kermit.Logger
import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.model.response.AlarmDetail
import com.dh.ondot.domain.service.AlarmScheduler

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val logger = Logger.withTag("AndroidAlarmScheduler")

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun scheduleAlarm(alarm: AlarmDetail, type: AlarmType) {
        // 사용자가 알람을 꺼두었다면 리턴
        if (!alarm.enabled) return

        // Content.ALARM_SERVICE 를 통해 시스템의 AlarmManager 인스턴스를 꺼냄
        // 이 인스턴스가 실제 OS 차원 알람 예약을 담당
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val now = System.currentTimeMillis()
        // "2025-05-10T18:30:00" 문자열을 Instant 로 파싱
        // toEpochMilliseconds()로 밀리초로 변환 -> 이 밀리초 값을 기준으로 AlarmManager 가 예약
        val triggerAtMillis = DateTimeFormatter.isoStringToEpochMillis(iso = alarm.triggeredAt)

        if (triggerAtMillis <= now) {
            logger.d { "이미 지난 알람(${alarm.triggeredAt}) 은 예약하지 않습니다." }
            return
        }

        // AlarmReceiver(BroadcastReceiver)를 깨우기 위한 Intent
        // putExtra 를 통해 alarmId와 type을 전달
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarm.alarmId)
            putExtra("type", type.name)
        }

        // PendingIntent 를 생성
        // 고유한 requestCode 를 사용하면, 여러 알람이 충돌 없이 개별적으로 동작할 수 있음
        // flags: FLAG_UPDATE_CURRENT -> 기존 PendingIntent 가 있으면 새로운 Intent 로 업데이트
        // flags: FLAG_IMMUTABLE -> PendingIntent 를 수정할 수 없도록 설정
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.alarmId.toInt(), // alarmId를 PendingIntent 의 request code 로 사용
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // flags
        )

        // AlarmManager 에 알람 예약
        // setExtraAndAllowWhileIdle: Doze 모드에서도 알람이 예약되도록 설정
        // RTC_WAKEUP: 휴대폰 꺼진 스크린도 깨워서 예약 시간에 브로드캐스트를 보냄
        // triggerAtMillis: 알람이 예약될 시간
        // pendingIntent: 알람이 예약될 때 실행될 Intent
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )

        logger.d { "Alarm scheduled at ${triggerAtMillis}, detail: $alarm" }
    }

    override fun cancelAlarm(alarmId: Long) {
        // AlarmManager는 안드로이드 OS에 알람을 등록하거나 취소할 수 있는 시스템 서비스
        // 알람을 취소할 때는 등록할 때와 동일한 Intent 정보와 requestCode(alarmId.toInt())를 전달해서 취소 가능
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override fun snoozeAlarm(alarmId: Long) {
        // storage.alarmsFlow에서 알람 찾아 snoozeInterval만큼 뒤로 미룸
        // 다시 scheduleAlarm 호출
    }
}
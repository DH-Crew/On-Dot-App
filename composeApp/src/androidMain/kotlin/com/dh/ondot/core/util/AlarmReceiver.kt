package com.dh.ondot.core.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger
import com.dh.ondot.domain.model.enums.AlarmType

class AlarmReceiver : BroadcastReceiver() {
    private val logger = Logger.withTag("AlarmReceiver")

    override fun onReceive(context: Context?, intent: Intent?) {
        // context, intent 가 null 인 경우 예외 처리
        if (context == null || intent == null) {
            logger.e { "AlarmReceiver onReceive: context 또는 intent 가 null 입니다." }
            return
        }

        // Intent 에서 알람 정보 파싱
        val alarmId  = intent.getLongExtra("alarmId", -1L)
        val type     = intent.getStringExtra("type") ?: AlarmType.Departure.name

        // 알람 정보가 없는 경우 예외 처리
        if (alarmId == -1L) {
            logger.e { "AlarmReceiver onReceive: 알람 정보가 없습니다." }
            return
        }

        // AlarmService 실행해서 사운드 재생 + 화면 전환
        val svcIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_START
            putExtra("alarmId", alarmId)
            putExtra("type", type)
        }

        logger.d { "AlarmReceiver onReceive: $alarmId, $type" }

        try {
            ContextCompat.startForegroundService(context, svcIntent)
        } catch (e: Exception) {
            logger.e(e) { "AlarmReceiver onReceive: AlarmService 실행 실패" }
        }
    }
}
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
        // Intent 에서 알람 정보 파싱
        val alarmId  = intent?.getLongExtra("alarmId", -1L)
        val type     = intent?.getStringExtra("type") ?: AlarmType.Departure.name

        // AlarmService 실행해서 사운드 재생 + 화면 전환
        val svcIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("alarmId", alarmId)
            putExtra("type", type)
        }

        logger.d { "AlarmReceiver onReceive: $alarmId, $type" }

        context?.let { ContextCompat.startForegroundService(context, svcIntent) }
    }
}
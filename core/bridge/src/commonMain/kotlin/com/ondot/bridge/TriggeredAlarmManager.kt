package com.ondot.bridge

import co.touchlab.kermit.Logger
import com.ondot.bridge.DirectionsFacade.getKoin
import com.ondot.domain.model.enums.AlarmAction
import com.ondot.domain.model.request.alarm.TriggeredAlarmRequest
import com.ondot.domain.repository.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

object TriggeredAlarmManager {
    private val logger = Logger.withTag("AlarmEventBridge")
    private val repo: AlarmRepository = getKoin().get()

    fun recordTriggeredAlarm(
        scheduleId: Long,
        alarmId: Long,
        action: AlarmAction
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            repo.recordTriggeredAlarm(
                TriggeredAlarmRequest(
                    scheduleId = scheduleId,
                    alarmId = alarmId,
                    action = action
                )
            )
            .catch { e ->
                logger.e(e) { "알림 기록 실패" }
            }
            .collect { result ->
                result.onFailure { e ->
                    logger.e(e) { "알람 기록 실패" }
                }
            }
        }
    }
}
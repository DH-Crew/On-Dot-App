package com.ondot.domain.service

import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.ui.AlarmRingInfo

sealed interface AlarmScheduleResult {
    data class Success(
        val platformId: String? = null,
    ) : AlarmScheduleResult

    data class Failure(
        val reason: String? = null,
    ) : AlarmScheduleResult

    data object SkippedDisabled : AlarmScheduleResult // 알람이 비활성화된 경우

    data object Unauthorized : AlarmScheduleResult // AlarmKit 권한이 없는 경우

    data object InvalidDate : AlarmScheduleResult // 알람 설정 날짜가 잘못된 경우
}

sealed interface AlarmCancelResult {
    data object Success : AlarmCancelResult

    data object NotFound : AlarmCancelResult

    data class Failure(
        val reason: String? = null,
    ) : AlarmCancelResult
}

interface AlarmScheduler {
    suspend fun scheduleAlarm(
        info: AlarmRingInfo,
        mapProvider: MapProvider,
    ): AlarmScheduleResult

    suspend fun cancelAlarm(alarmId: Long): AlarmCancelResult

    suspend fun snoozeAlarm(alarmId: Long): AlarmScheduleResult
}

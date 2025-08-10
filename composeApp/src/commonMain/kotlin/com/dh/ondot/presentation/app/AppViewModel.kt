package com.dh.ondot.presentation.app

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.di.openDirections
import com.dh.ondot.core.di.stopService
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.core.util.DateTimeFormatter.plusMinutes
import com.dh.ondot.core.util.DateTimeFormatter.toLocalDateFromIso
import com.dh.ondot.domain.model.enums.MapProvider
import com.dh.ondot.domain.service.AlarmScheduler
import com.dh.ondot.domain.service.AlarmStorage
import com.dh.ondot.domain.service.SoundPlayer
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.ui.theme.ANDROID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AppViewModel(
    private val alarmScheduler: AlarmScheduler = ServiceLocator.provideAlarmScheduler(),
    private val alarmStorage: AlarmStorage = ServiceLocator.provideAlarmStorage(),
    private val soundPlayer: SoundPlayer = ServiceLocator.provideSoundPlayer()
): BaseViewModel<AppUiState>(AppUiState()) {
    private val logger = Logger.withTag("AppViewModel")

    fun getAlarmInfo(alarmId: Long) {
        viewModelScope.launch {
            val allAlarms = alarmStorage.alarmsFlow.first()

            logger.d { "allAlarms: $allAlarms" }

            val alarm = allAlarms.firstOrNull { it.alarmDetail.alarmId == alarmId } ?: return@launch

            logger.d { "alarm: $alarm" }

            updateStateSync(uiState.value.copy(alarmRingInfo = alarm))
        }
    }

    fun startPreparation() {
        soundPlayer.stopSound()
        updateState(uiState.value.copy(showPreparationStartAnimation = true))
    }

    fun snoozePreparationAlarm() {
        soundPlayer.stopSound()
        processAlarm()
        updateState(uiState.value.copy(showPreparationSnoozeAnimation = true))
    }

    fun snoozeDepartureAlarm() {
        soundPlayer.stopSound()
        processAlarm()
        updateState(uiState.value.copy(showDepartureSnoozeAnimation = true))
    }

    fun startDeparture() {
        soundPlayer.stopSound()

        if (getPlatform().name == ANDROID) stopService(uiState.value.alarmRingInfo.alarmDetail.alarmId)

        val info = uiState.value.alarmRingInfo
        val invalidCoords = listOf(info.startLat, info.startLng, info.endLat, info.endLng).any { it.isNaN() }
                || ((info.startLat == 0.0 && info.startLng == 0.0) || (info.endLat == 0.0 && info.endLng == 0.0))
        if (invalidCoords) {
            logger.e { "Invalid coords: $invalidCoords" }
            return
        }

        emitEventFlow(AppEvent.NavigateToSplash)
        openDirections(
            startLat = uiState.value.alarmRingInfo.startLat,
            startLng = uiState.value.alarmRingInfo.startLng,
            endLat = uiState.value.alarmRingInfo.endLat,
            endLng = uiState.value.alarmRingInfo.endLng,
            startName = "출발지",
            endName = "도착지",
            provider = MapProvider.Kakao
        )
    }

    private fun processAlarm() {
        var newAlarmInfo = uiState.value.alarmRingInfo
        val currentTriggeredDate = newAlarmInfo.alarmDetail.triggeredAt.toLocalDateFromIso()
        val currentTriggeredTime = Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Seoul")).time.plusMinutes(uiState.value.alarmRingInfo.alarmDetail.snoozeInterval)
        val newTriggeredAt = DateTimeFormatter.formatIsoDateTime(currentTriggeredDate, currentTriggeredTime)

        newAlarmInfo = newAlarmInfo.copy(
            alarmDetail = uiState.value.alarmRingInfo.alarmDetail.copy(
                triggeredAt = newTriggeredAt
            )
        )

        viewModelScope.launch {
            // 저장소에 저장
            alarmStorage.addAlarm(newAlarmInfo)

            // 스케줄러 예약
            alarmScheduler.scheduleAlarm(newAlarmInfo.alarmDetail, newAlarmInfo.alarmType)
        }
    }
}
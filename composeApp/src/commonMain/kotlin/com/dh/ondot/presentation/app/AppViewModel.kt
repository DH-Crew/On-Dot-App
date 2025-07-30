package com.dh.ondot.presentation.app

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.domain.service.AlarmScheduler
import com.dh.ondot.domain.service.AlarmStorage
import com.dh.ondot.domain.service.SoundPlayer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    fun onPreparationStart() {
        soundPlayer.stopSound()
        updateState(uiState.value.copy(showPreparationStartAnimation = true))
    }

    fun snoozeDepartureAlarm() {
        soundPlayer.stopSound()
        updateState(uiState.value.copy(showDepartureSnoozeAnimation = true))
    }
}
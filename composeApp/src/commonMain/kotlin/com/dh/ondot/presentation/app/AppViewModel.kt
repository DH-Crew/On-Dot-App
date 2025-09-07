package com.dh.ondot.presentation.app

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.platform.openDirections
import com.dh.ondot.core.platform.stopService
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.core.util.DateTimeFormatter.plusMinutes
import com.dh.ondot.core.util.DateTimeFormatter.toLocalDateFromIso
import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.domain.service.AlarmScheduler
import com.dh.ondot.domain.service.AlarmStorage
import com.dh.ondot.domain.service.SoundPlayer
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.ui.theme.ANDROID
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AppViewModel(
    private val alarmScheduler: AlarmScheduler = ServiceLocator.provideAlarmScheduler(),
    private val alarmStorage: AlarmStorage = ServiceLocator.provideAlarmStorage(),
    private val soundPlayer: SoundPlayer = ServiceLocator.provideSoundPlayer(),
    private val memberRepository: MemberRepository = ServiceLocator.memberRepository,
    private val scheduleRepository: ScheduleRepository = ServiceLocator.scheduleRepository
): BaseViewModel<AppUiState>(AppUiState()) {
    private val logger = Logger.withTag("AppViewModel")

    init {
        initMapProvider()
    }

    private fun initMapProvider() {
        viewModelScope.launch {
            memberRepository.getLocalMapProvider().collect {
                updateState(uiState.value.copy(mapProvider = it))
            }
        }
    }

    fun getAlarmInfo(scheduleId: Long, alarmId: Long) {
        viewModelScope.launch {
            scheduleRepository.getLocalScheduleById(scheduleId).collect { schedule ->
                schedule?.let {
                    val currentAlarm = if (schedule.preparationAlarm.alarmId == alarmId) schedule.preparationAlarm
                    else schedule.departureAlarm

                    updateState(
                        uiState.value.copy(schedule = schedule, currentAlarm = currentAlarm)
                    )
                }
            }
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

        if (getPlatform().name == ANDROID) stopService(uiState.value.currentAlarm.alarmId)

        val info = uiState.value.schedule
        val invalidCoords = listOf(info.startLatitude, info.startLongitude, info.endLatitude, info.endLongitude).any { it.isNaN() }
                || ((info.startLatitude == 0.0 && info.startLongitude == 0.0) || (info.endLatitude == 0.0 && info.endLongitude == 0.0))
        if (invalidCoords) {
            logger.e { "Invalid coords: $invalidCoords" }
            return
        }

        emitEventFlow(AppEvent.NavigateToSplash)
        openDirections(
            startLat = info.startLatitude,
            startLng = info.startLongitude,
            endLat = info.endLatitude,
            endLng = info.endLongitude,
            startName = "출발지",
            endName = "도착지",
            provider = uiState.value.mapProvider
        )
    }

    fun initAnimationFlags() {
        updateState(uiState.value.copy(
            showPreparationSnoozeAnimation = false,
            showDepartureSnoozeAnimation = false
        ))
    }

    private fun processAlarm() {
        var newSchedule = uiState.value.schedule
        var currentAlarm = uiState.value.currentAlarm
        val currentTriggeredDate = currentAlarm.triggeredAt.toLocalDateFromIso()
        val currentTriggeredTime = Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Seoul")).time.plusMinutes(currentAlarm.snoozeInterval)
        val newTriggeredAt = DateTimeFormatter.formatIsoDateTime(currentTriggeredDate, currentTriggeredTime)
        val type = if (currentAlarm.alarmId == newSchedule.preparationAlarm.alarmId) AlarmType.Preparation else AlarmType.Departure

        currentAlarm = when(type) {
            AlarmType.Departure -> newSchedule.departureAlarm.copy(
                triggeredAt = newTriggeredAt
            )
            AlarmType.Preparation -> newSchedule.preparationAlarm.copy(
                triggeredAt = newTriggeredAt
            )
        }

        newSchedule = when(type) {
            AlarmType.Departure -> newSchedule.copy(departureAlarm = currentAlarm)
            AlarmType.Preparation -> newSchedule.copy(preparationAlarm = currentAlarm)
        }

        viewModelScope.launch {
            // 저장소에 저장
            scheduleRepository.upsertLocalSchedule(newSchedule)

            // 스케줄러 예약
            alarmScheduler.scheduleAlarm(newSchedule.scheduleId, currentAlarm, type)
        }
    }
}
package com.dh.ondot.presentation.app

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.DirectionsFacade
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_PREPARATION
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.schedule.SchedulePreparation
import com.ondot.domain.model.ui.AlarmRingInfo
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.domain.service.AlarmScheduler
import com.ondot.domain.service.AnalyticsManager
import com.ondot.domain.service.SoundPlayer
import com.ondot.util.DateTimeFormatter
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

class AppViewModel(
    private val alarmScheduler: AlarmScheduler,
    private val soundPlayer: SoundPlayer,
    private val memberRepository: MemberRepository,
    private val scheduleRepository: ScheduleRepository,
    private val analyticsManager: AnalyticsManager
): BaseViewModel<AppUiState>(AppUiState()) {
    private val logger = Logger.withTag("AppViewModel")

    private fun logGA(name: String, vararg params: Pair<String, Any?>) {
        val clean = params.toMap().filterValues { it != null }
        analyticsManager.logEvent(name, clean)
    }

    init {
        initMapProvider()
    }

    private fun initMapProvider() {
        viewModelScope.launch {
            memberRepository.getLocalMapProvider().collect {
                updateState(uiState.value.copy(mapProvider = it))

                analyticsManager.setUserProperty("map_provider", it.toString())
            }
        }
    }

    fun getAlarmInfo(scheduleId: Long, alarmId: Long) {
        viewModelScope.launch {
            scheduleRepository.getLocalScheduleById(scheduleId).collect { schedule ->
                schedule?.let {
                    val currentAlarm = when(alarmId) {
                        it.preparationAlarm.alarmId -> it.preparationAlarm
                        it.departureAlarm.alarmId -> it.departureAlarm
                        else -> {
                            logger.e { "유효하지 않은 알람: $alarmId" }
                            return@collect
                        }
                    }

                    getSchedulePreparation(scheduleId)

                    updateState(
                        uiState.value.copy(schedule = schedule, currentAlarm = currentAlarm)
                    )

                    val type = if (alarmId == it.preparationAlarm.alarmId) "preparation" else "departure"
                    logGA(
                        "alarm_open",
                        "schedule_id" to it.scheduleId,
                        "alarm_id" to currentAlarm.alarmId,
                        "alarm_type" to type,
                        "triggered_at" to currentAlarm.triggeredAt
                    )
                }
            }
        }
    }

    fun startPreparation() {
        soundPlayer.stopSound()
        updateState(uiState.value.copy(showPreparationStartAnimation = true))

        logGA("preparation_start")
    }

    fun snoozePreparationAlarm() {
        soundPlayer.stopSound()
        snoozeAlarm()
        updateState(uiState.value.copy(showPreparationSnoozeAnimation = true))

        logGA(
            "alarm_snooze_click",
            "alarm_type" to "preparation",
        )
    }

    fun snoozeDepartureAlarm() {
        soundPlayer.stopSound()
        snoozeAlarm()
        updateState(uiState.value.copy(showDepartureSnoozeAnimation = true))

        logGA(
            "alarm_snooze_click",
            "alarm_type" to "departure",
        )
    }

    fun startDeparture() {
        soundPlayer.stopSound()

        val info = uiState.value.schedule
        val invalidCoords = listOf(info.startLatitude, info.startLongitude, info.endLatitude, info.endLongitude).any { it.isNaN() }
                || ((info.startLatitude == 0.0 && info.startLongitude == 0.0) || (info.endLatitude == 0.0 && info.endLongitude == 0.0))
        if (invalidCoords) {
            logger.e { "Invalid coords: $invalidCoords" }
            return
        }

        logGA(
            "directions_open",
            "schedule_id" to info.scheduleId,
            "provider" to uiState.value.mapProvider.toString().lowercase()
        )

        emitEventFlow(AppEvent.NavigateToSplash)
        DirectionsFacade.openDirections(
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

    @OptIn(ExperimentalTime::class)
    private fun snoozeAlarm() {
        var newSchedule = uiState.value.schedule
        var currentAlarm = uiState.value.currentAlarm
        val timeZone = TimeZone.of("Asia/Seoul")
        val snoozedLocal = kotlin.time.Clock.System.now()
            .plus(currentAlarm.snoozeInterval.toLong(), DateTimeUnit.MINUTE, timeZone)
            .toLocalDateTime(timeZone)
        val newTriggeredAt = DateTimeFormatter.formatIsoDateTime(snoozedLocal.date, snoozedLocal.time)
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
            alarmScheduler.scheduleAlarm(
                AlarmRingInfo(
                    scheduleId = newSchedule.scheduleId,
                    alarm = currentAlarm,
                    alarmType = type,
                    startLat = newSchedule.startLatitude,
                    startLng = newSchedule.startLongitude,
                    endLat = newSchedule.endLatitude,
                    endLng = newSchedule.endLongitude,
                    scheduleTitle = newSchedule.scheduleTitle,
                    appointmentAt = newSchedule.appointmentAt
                ),
                mapProvider = uiState.value.mapProvider
            )

            logGA(
                "alarm_snoozed",
                "schedule_id" to newSchedule.scheduleId,
                "alarm_id" to currentAlarm.alarmId,
                "alarm_type" to type.name.lowercase(),
                "snooze_interval_min" to currentAlarm.snoozeInterval,
                "next_triggered_at" to newTriggeredAt
            )
        }
    }

    /**--------------------------------------------일정 준비 정보 조회-----------------------------------------------*/

    fun getSchedulePreparation(scheduleId: Long) {
        viewModelScope.launch {
            scheduleRepository.getSchedulePreparationInfo(scheduleId = scheduleId).collect {
                resultResponse(it, ::onSuccessGetSchedulePreparation, ::onFailGetSchedulePreparation)
            }
        }
    }

    private fun onSuccessGetSchedulePreparation(result: SchedulePreparation) {
        updateState(uiState.value.copy(schedulePreparation = result))
    }

    private fun onFailGetSchedulePreparation(e: Throwable) {
        logger.e { "onFailGetSchedulePreparation: $e" }
        viewModelScope.launch { ToastManager.show(ERROR_GET_SCHEDULE_PREPARATION, ToastType.ERROR) }
    }
}
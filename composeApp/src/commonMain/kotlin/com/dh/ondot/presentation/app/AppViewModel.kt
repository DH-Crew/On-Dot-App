package com.dh.ondot.presentation.app

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.DirectionsFacade
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_PREPARATION
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.schedule.Schedule
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
import kotlin.time.Clock
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

    @OptIn(ExperimentalTime::class)
    fun startDeparture() {
        soundPlayer.stopSound()

        val schedule = uiState.value.schedule
        val invalidCoords = listOf(schedule.startLatitude, schedule.startLongitude, schedule.endLatitude, schedule.endLongitude).any { it.isNaN() }
                || ((schedule.startLatitude == 0.0 && schedule.startLongitude == 0.0) || (schedule.endLatitude == 0.0 && schedule.endLongitude == 0.0))
        if (invalidCoords) {
            logger.e { "Invalid coords: $invalidCoords" }
            return
        }

        val now = Clock.System.now()
        val koreaZone = TimeZone.of("Asia/Seoul")
        val localDateTime = now.toLocalDateTime(koreaZone)
        val isoDate = localDateTime.toString()
        val epochMs = now.toEpochMilliseconds()

        logGA(
            "departure_alarm_off",
            "occurred_at_iso" to isoDate,
            "occurred_at_ms" to epochMs
        )

        logGA(
            "directions_open",
            "schedule_id" to schedule.scheduleId,
            "provider" to uiState.value.mapProvider.toString().lowercase()
        )

        if (schedule.isRepeat) scheduleNextAlarm(schedule)

        emitEventFlow(AppEvent.NavigateToHome)
        DirectionsFacade.openDirections(
            startLat = schedule.startLatitude,
            startLng = schedule.startLongitude,
            endLat = schedule.endLatitude,
            endLng = schedule.endLongitude,
            startName = "출발지",
            endName = "도착지",
            provider = uiState.value.mapProvider
        )
    }

    private fun scheduleNextAlarm(schedule: Schedule) {
        // 일: 1, 월: 2 ... 토: 7
        val repeatDays = schedule.repeatDays
        if (repeatDays.isEmpty()) return
        // 현재 알람이 울린 일정의 요일 정보
        val currentDayOfWeek = DateTimeFormatter.extractDayOfWeek(schedule.appointmentAt)

        // currentDayOfWeek 이후에 오는 가장 가까운 요일 찾기
        val candidate = repeatDays.firstOrNull { it > currentDayOfWeek } ?: repeatDays.first()

        // 요일 간 차이를 일수로 변환 (0이면 다음 주)
        var daysToAdd = (candidate - currentDayOfWeek + 7) % 7
        if (daysToAdd == 0) daysToAdd = 7

        // appointmentAt, 준비/출발 알람 시간을 모두 daysToAdd 만큼 밀기
        val nextAppointmentAt = DateTimeFormatter.plusDays(schedule.appointmentAt, daysToAdd)
        val nextPreparationAt = DateTimeFormatter.plusDays(schedule.preparationAlarm.triggeredAt, daysToAdd)
        val nextDepartureAt = DateTimeFormatter.plusDays(schedule.departureAlarm.triggeredAt, daysToAdd)

        // 다음 스케줄 객체 생성
        val nextSchedule = schedule.copy(
            appointmentAt = nextAppointmentAt,
            preparationAlarm = schedule.preparationAlarm.copy(
                triggeredAt = nextPreparationAt
            ),
            departureAlarm = schedule.departureAlarm.copy(
                triggeredAt = nextDepartureAt
            )
        )

        viewModelScope.launch {
            scheduleRepository.upsertLocalSchedule(nextSchedule)

            if (nextSchedule.preparationAlarm.enabled) {
                val alarmInfo = AlarmRingInfo(
                    alarm = nextSchedule.preparationAlarm,
                    alarmType = AlarmType.Preparation,
                    appointmentAt = nextSchedule.appointmentAt,
                    scheduleTitle = nextSchedule.scheduleTitle,
                    scheduleId = nextSchedule.scheduleId,
                    startLat = nextSchedule.startLatitude,
                    startLng = nextSchedule.startLongitude,
                    endLat = nextSchedule.endLatitude,
                    endLng = nextSchedule.endLongitude
                )

                alarmScheduler.scheduleAlarm(alarmInfo, uiState.value.mapProvider)
            }

            val alarmInfo = AlarmRingInfo(
                alarm = nextSchedule.departureAlarm,
                alarmType = AlarmType.Departure,
                appointmentAt = nextSchedule.appointmentAt,
                scheduleTitle = nextSchedule.scheduleTitle,
                scheduleId = nextSchedule.scheduleId,
                startLat = nextSchedule.startLatitude,
                startLng = nextSchedule.startLongitude,
                endLat = nextSchedule.endLatitude,
                endLng = nextSchedule.endLongitude
            )
            alarmScheduler.scheduleAlarm(alarmInfo, uiState.value.mapProvider)
        }
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
        val snoozedLocal = Clock.System.now()
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
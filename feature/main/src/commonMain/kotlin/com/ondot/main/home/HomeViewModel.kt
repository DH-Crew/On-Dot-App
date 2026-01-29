package com.ondot.main.home

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.presentation.ui.theme.ERROR_DELETE_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_LIST
import com.dh.ondot.presentation.ui.theme.ERROR_SET_MAP_PROVIDER
import com.dh.ondot.presentation.ui.theme.NOTIFICATION_TITLE
import com.dh.ondot.presentation.ui.theme.SUCCESS_DELETE_SCHEDULE
import com.ondot.bridge.TriggeredAlarmManager
import com.ondot.domain.model.enums.AlarmAction
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.request.MapProviderRequest
import com.ondot.domain.model.request.ToggleAlarmRequest
import com.ondot.domain.model.request.local_notification.LocalNotificationRequest
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.model.schedule.ScheduleList
import com.ondot.domain.model.ui.AlarmRingInfo
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.domain.service.AlarmScheduler
import com.ondot.domain.service.AnalyticsManager
import com.ondot.domain.service.DirectionsOpener
import com.ondot.domain.service.LocalNotificationScheduler
import com.ondot.ui.base.BaseViewModel
import com.ondot.ui.util.ToastManager
import com.ondot.util.DateTimeFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val memberRepository: MemberRepository,
    private val alarmScheduler: AlarmScheduler,
    private val analyticsManager: AnalyticsManager,
    private val notificationScheduler: LocalNotificationScheduler,
    private val directionsOpener: DirectionsOpener
) : BaseViewModel<HomeUiState>(HomeUiState()) {
    private val logger = Logger.withTag("HomeViewModel")
    private var mapProvider = MapProvider.KAKAO
    private var lastScheduledAlarms: Set<Long> = emptySet()

    private var remainingTimeJob: Job? = null
    private var earliestAlarmAt: String? = null

    init {
        needsChooseProvider()
        observeMapProvider()
    }

    private fun needsChooseProvider() {
        viewModelScope.launch {
            memberRepository.needsChooseProvider().collect {
                logger.e { "needsChooseProvider: $it" }
                updateState(uiState.value.copy(needsChooseProvider = it))
            }
        }
    }

    private fun observeMapProvider() = viewModelScope.launch {
        memberRepository.getLocalMapProvider().collect {
            logger.e { "mapProvider: $it" }
            mapProvider = it
        }
    }

    fun onToggle() {
        updateStateSync(uiState.value.copy(isExpanded = !uiState.value.isExpanded))
    }

    fun onClickAlarmSwitch(id: Long, isEnabled: Boolean) {
        val schedule = uiState.value.scheduleList.find { it.scheduleId == id } ?: return

        val newList = uiState.value.scheduleList.map {
            if (it.scheduleId == id) {
                it.copy(
                    hasActiveAlarm = isEnabled,
                    departureAlarm = it.departureAlarm.copy(enabled = isEnabled),
                    preparationAlarm = it.preparationAlarm.copy(enabled = isEnabled)
                )
            } else {
                it
            }
        }

        updateState(uiState.value.copy(scheduleList = newList))

        if (isEnabled) {
            processAlarmsForSchedule(schedule.copy(
                departureAlarm = schedule.departureAlarm.copy(enabled = true),
                preparationAlarm = schedule.preparationAlarm.copy(enabled = true)
            ))
            lastScheduledAlarms = lastScheduledAlarms + setOf(schedule.departureAlarm.alarmId, schedule.preparationAlarm.alarmId)
        } else {
            cancelAlarms(id)
            lastScheduledAlarms = lastScheduledAlarms - setOf(schedule.departureAlarm.alarmId, schedule.preparationAlarm.alarmId)
        }

        viewModelScope.launch {
            scheduleRepository.toggleAlarm(scheduleId = id, request = ToggleAlarmRequest(isEnabled = isEnabled)).collect {
                resultResponse(it, {})
            }
            getScheduleList()
        }
    }

    /**----------------------------------------------스케줄 조회---------------------------------------------*/

    fun getScheduleList() {
        viewModelScope.launch {
            scheduleRepository.getScheduleList().collect {
                resultResponse(it, ::onSuccessGetScheduleList, ::onFailureGetScheduleList)
            }
        }
    }

    private fun onSuccessGetScheduleList(result: ScheduleList) {
        earliestAlarmAt = result.earliestAlarmAt.ifBlank { null }

        val remainingTime = if (result.earliestAlarmAt.isNotBlank()) {
            DateTimeFormatter.calculateRemainingTime(result.earliestAlarmAt)
        } else { Triple(-1, -1, -1) }

        updateState(
            uiState.value.copy(
                remainingTime = remainingTime,
                scheduleList = result.scheduleList,
                earliestScheduleId = result.earliestScheduleId,
            )
        )

        startRemainingTimeTicker()

        processAlarms(result.scheduleList)
        scheduleNotification(result.scheduleList)
    }

    private fun processAlarms(schedules: List<Schedule>) {
        viewModelScope.launch {

            // 현재 스케줄 리스트를 기반으로 울려야 하는 알람 id를 추출
            val currentAlarmIds = schedules.flatMap { schedule ->
                buildList {
                    if (schedule.preparationAlarm.enabled) add(schedule.preparationAlarm.alarmId)
                    if (schedule.departureAlarm.enabled) add(schedule.departureAlarm.alarmId)
                }
            }.toSet()

            // 삭제할 알람: 이전에는 있었지만 현재는 없는 것
            val toCancel = lastScheduledAlarms - currentAlarmIds
            toCancel.forEach { alarmId -> alarmScheduler.cancelAlarm(alarmId) }

            // 추가/업데이트할 알람: 현재 있는 것만
            val alarmRingInfos = schedules.flatMap { schedule ->
                buildList {
                    if (schedule.preparationAlarm.enabled) {
                        add(prepInfo(schedule))
                    }
                    if (schedule.departureAlarm.enabled) {
                        add(depInfo(schedule))
                    }
                }
            }

            // 스케줄러 예약
            alarmRingInfos.forEach { info ->
                alarmScheduler.scheduleAlarm(info = info, mapProvider = mapProvider)
                TriggeredAlarmManager.recordTriggeredAlarm(info.scheduleId, info.alarm.alarmId, AlarmAction.SCHEDULED)
            }

            // 이전 스케줄 리스트를 업데이트
            lastScheduledAlarms = currentAlarmIds
        }
    }

    private fun processAlarmsForSchedule(schedule: Schedule) {
        viewModelScope.launch {
            val alarmRingInfos = buildList {
                if (schedule.preparationAlarm.enabled) {
                    add(prepInfo(schedule))
                }
                if (schedule.departureAlarm.enabled) {
                    add(depInfo(schedule))
                }
            }

            alarmRingInfos.forEach { info ->
                alarmScheduler.scheduleAlarm(info = info, mapProvider = mapProvider)
                TriggeredAlarmManager.recordTriggeredAlarm(info.scheduleId, info.alarm.alarmId, AlarmAction.SCHEDULED)
            }
        }
    }

    private fun scheduleNotification(schedules: List<Schedule>) {
        val twoMinutesMillis = 2 * 60 * 1000L

        schedules.forEach { schedule ->
            if (schedule.preparationNote.isNotBlank()) {
                notificationScheduler.schedule(
                    request = LocalNotificationRequest(
                        id = schedule.scheduleId.toString(),
                        title = NOTIFICATION_TITLE,
                        body = schedule.preparationNote,
                        triggerAtMillis = DateTimeFormatter.isoStringToEpochMillis(schedule.departureAlarm.triggeredAt) - twoMinutesMillis
                    )
                )
            }
        }
    }

    private fun onFailureGetScheduleList(throwable: Throwable) {
        logger.e { throwable.message.toString() }

        viewModelScope.launch { ToastManager.show(message = ERROR_GET_SCHEDULE_LIST, type = ToastType.ERROR) }
    }

    fun setMapProvider(mapProvider: MapProvider) {
        viewModelScope.launch {
            memberRepository.updateMapProvider(request = MapProviderRequest(mapProvider)).collect {
                resultResponse(
                    it,
                    {
                        updateState(uiState.value.copy(needsChooseProvider = false))
                    },
                    {
                        logger.e { it.message.toString() }
                        viewModelScope.launch { ToastManager.show(message = ERROR_SET_MAP_PROVIDER, type = ToastType.ERROR) }
                    }
                )
            }
        }
    }

    /**----------------------------------------------일정 삭제---------------------------------------------*/

    fun deleteSchedule(scheduleId: Long) {
        val curList = uiState.value.scheduleList
        val newList = uiState.value.scheduleList.filter { it.scheduleId != scheduleId }
        val job = viewModelScope.launch {
            delay(2000)
            scheduleRepository.deleteSchedule(scheduleId).collect {
                resultResponse(it, { onSuccessDeleteSchedule(scheduleId) }, ::onFailDeleteSchedule)
            }
        }

        cancelAlarms(scheduleId)
        updateStateSync(uiState.value.copy(scheduleList = newList))

        viewModelScope.launch {
            ToastManager.show(
                message = SUCCESS_DELETE_SCHEDULE,
                type = ToastType.DELETE,
                callback = {
                    job.cancel()
                    updateState(uiState.value.copy(scheduleList = curList))
                }
            )
        }
    }

    private fun onSuccessDeleteSchedule(scheduleId: Long) {
        notificationScheduler.cancel(scheduleId.toString())
        updateState(uiState.value.copy(scheduleList = uiState.value.scheduleList.filter { it.scheduleId != scheduleId }))
        getScheduleList()
    }

    private fun onFailDeleteSchedule(e: Throwable) {
        logger.e { e.message.toString() }
        viewModelScope.launch {
            ToastManager.show(ERROR_DELETE_SCHEDULE, ToastType.ERROR)
            getScheduleList()
        }
    }

    /**--------------------------------------------알람 취소-----------------------------------------------*/

    /**홈 화면에서 일정을 삭제할 때 스케줄링된 알람도 함께 삭제*/
    private fun cancelAlarms(scheduleId: Long) {
        val schedule = uiState.value.scheduleList.find { it.scheduleId == scheduleId }
        schedule?.let {
            alarmScheduler.cancelAlarm(it.departureAlarm.alarmId)
            alarmScheduler.cancelAlarm(it.preparationAlarm.alarmId)
        }
    }

    /**--------------------------------------------경로 안내-----------------------------------------------*/

    fun openDirections(id: Long) {
        val schedule = uiState.value.scheduleList.first { it.scheduleId == id }

        directionsOpener.openDirections(
            startLat = schedule.startLatitude,
            startLng = schedule.startLongitude,
            endLat = schedule.endLatitude,
            endLng = schedule.endLongitude,
            provider = mapProvider,
            startName = "출발지",
            endName = "도착지"
        )
    }

    /**--------------------------------------------기타 유틸-----------------------------------------------*/

    private fun prepInfo(schedule: Schedule) = AlarmRingInfo(
        alarm = schedule.preparationAlarm,
        alarmType = AlarmType.Preparation,
        appointmentAt = schedule.appointmentAt,
        scheduleTitle = schedule.scheduleTitle,
        scheduleId = schedule.scheduleId,
        startLat = schedule.startLatitude,
        startLng = schedule.startLongitude,
        endLat = schedule.endLatitude,
        endLng = schedule.endLongitude,
        repeatDays = schedule.repeatDays
    )

    private fun depInfo(schedule: Schedule) = AlarmRingInfo(
        alarm = schedule.departureAlarm,
        alarmType = AlarmType.Departure,
        appointmentAt = schedule.appointmentAt,
        scheduleTitle = schedule.scheduleTitle,
        scheduleId = schedule.scheduleId,
        startLat = schedule.startLatitude,
        startLng = schedule.startLongitude,
        endLat = schedule.endLatitude,
        endLng = schedule.endLongitude,
        repeatDays = schedule.repeatDays
    )

    private fun startRemainingTimeTicker() {
        val target = earliestAlarmAt
        if (target == null) {
            remainingTimeJob?.cancel()
            remainingTimeJob = null
            return
        }

        remainingTimeJob?.cancel()
        remainingTimeJob = viewModelScope.launch {
            while (isActive) {
                val newRemaining = DateTimeFormatter.calculateRemainingTime(target)

                // 이미 0 이하라면 한 번 업데이트만 하고 종료
                if (newRemaining.first <= 0 && newRemaining.second <= 0 && newRemaining.third <= 0) {
                    updateStateSync(uiState.value.copy(remainingTime = newRemaining))
                    break
                }

                // 매 분 갱신
                updateStateSync(uiState.value.copy(remainingTime = newRemaining))
                delay(60_000L)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        remainingTimeJob?.cancel()
    }
}